package nablarch.integration.mail.thymeleaf;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import nablarch.common.mail.TemplateEngineProcessedResult;
import nablarch.common.mail.TemplateEngineProcessingException;

/**
 * {@link ThymeleafMailProcessor}のテストクラス。
 */
public class ThymeleafMailProcessorTest {

    private final ThymeleafMailProcessor sut = new ThymeleafMailProcessor();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final String template = "件名テスト：[(${foo})]\r\n---\r\n本文テスト１：[(${foo})]\r\n本文テスト２：[(${bar})]\r\n";
    private final String alterDelimiterTemplate = "---\r\n@@@\r\nAlter delimiter test.";

    /**
     * テンプレートエンジンの処理の確認。
     */
    @Test
    public void testProcess() {
        String lang = null;
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("foo", "hello");
        variables.put("bar", 123);

        //templateIdをそのままテンプレートとして扱う設定でテストをしているため、
        //テンプレートそのものを渡している。
        //上記の設定にしている理由はsetUpメソッド内のコメントを参照。
        TemplateEngineProcessedResult result = sut.process(template, lang, variables);

        assertThat(result.getSubject(), is("件名テスト：hello"));
        assertThat(result.getMailBody(), is("本文テスト１：hello\r\n本文テスト２：123\r\n"));
    }

    /**
     * 発生した{@link TemplateEngineException}を{@link TemplateEngineProcessingException}でラップすること。
     */
    @Test
    public void testProcess_template_engine_exception() {
        expectedException.expect(TemplateEngineProcessingException.class);
        expectedException.expectCause(isA(TemplateInputException.class));

        //文法が不正な場合、TemplateInputExceptionが投げられる。
        sut.process("[($IllegalGrammar)]", null, Collections.<String, Object> emptyMap());
    }

    /**
     * デリミタを変更した場合の確認。
     */
    @Test
    public void testProcess_alter_delimiter() {
        String lang = null;
        Map<String, Object> variables = Collections.emptyMap();

        sut.setDelimiter("@@@");

        //templateIdをそのままテンプレートとして扱う設定でテストをしているため、テンプレートそのものを渡している。
        //上記の設定にしている理由はsetUpメソッド内のコメントを参照。
        TemplateEngineProcessedResult result = sut.process(alterDelimiterTemplate, lang, variables);

        assertThat(result.getSubject(), is("---"));
        assertThat(result.getMailBody(), is("Alter delimiter test."));
    }

    @Before
    public void setUp() {
        TemplateEngine templateEngine = new TemplateEngine();

        //渡されたテンプレート名をそのままテンプレートとして扱うITemplateResolverを設定する。
        //そのような設定をしている理由は以下の通り。
        //  ・Thymeleafそのもののテストではなく、ThymeleafMailProcessorのテストである
        //  ・ユニットテストはなるべく外部リソースを使わずJVMに閉じている方がよい
        templateEngine.setTemplateResolver(new StringTemplateResolver());

        sut.setTemplateEngine(templateEngine);
    }
}
