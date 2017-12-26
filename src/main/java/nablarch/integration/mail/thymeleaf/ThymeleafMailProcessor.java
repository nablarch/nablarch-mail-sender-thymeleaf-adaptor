package nablarch.integration.mail.thymeleaf;

import java.util.Map;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateEngineException;

import nablarch.common.mail.TemplateEngineMailProcessor;
import nablarch.common.mail.TemplateEngineProcessedResult;
import nablarch.common.mail.TemplateEngineProcessingException;

/**
 * Thymeleafを使用する{@link TemplateEngineMailProcessor}の実装クラス。
 * 
 * @author Taichi Uragami
 *
 */
public class ThymeleafMailProcessor implements TemplateEngineMailProcessor {

    /** Thymeleafのテンプレートエンジン */
    private ITemplateEngine templateEngine;

    /** 件名と本文を分けるデリミタ */
    private String delimiter;

    /**
     * テンプレートIDから取得されたテンプレートと変数をマージして、その結果を返す。
     * 
     * <p>
     * テンプレートの検索、テンプレートと変数のマージは{@link ITemplateEngine#process(String, IContext)}が使われる。
     * </p>
     * 
     * <p>
     * ※この実装ではテンプレートの検索が多言語対応していないため、第二引数の言語は使用されない。
     * </p>
     * 
     * @see ITemplateEngine#process(String, IContext)
     */
    @Override
    public TemplateEngineProcessedResult process(String templateId, String lang,
            Map<String, Object> variables) {
        try {
            IContext context = createContext(variables);
            String processed = templateEngine.process(templateId, context);
            if (delimiter != null) {
                return TemplateEngineProcessedResult.valueOf(processed, delimiter);
            }
            return TemplateEngineProcessedResult.valueOf(processed);
        } catch (TemplateEngineException e) {
            throw new TemplateEngineProcessingException(e);
        }
    }

    /**
     * {@link IContext}を作成する。
     * 
     * @param variables {@link #process(String, String, Map)}に渡された変数
     * @return {@link IContext}のインスタンス
     */
    protected IContext createContext(Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return context;
    }

    /**
     * Thymeleafのエントリーポイントとなる{@link ITemplateEngine}を設定する。
     * 
     * @param templateEngine Thymeleafのテンプレートエンジン
     */
    public void setTemplateEngine(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * 件名と本文を分けるデリミタを設定する。
     * 
     * <p>
     * なにも設定されていなければ{@link TemplateEngineProcessedResult#DEFAULT_DELIMITER デフォルトのデリミタ}が使用される。
     * </p>
     * 
     * @param delimiter 件名と本文を分けるデリミタ
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
