package nablarch.integration.mail.thymeleaf;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import nablarch.common.mail.TemplateEngineProcessedResult;
import nablarch.core.repository.SystemRepository;
import nablarch.test.support.SystemRepositoryResource;

/**
 * {@link ThymeleafMailProcessor}をコンポーネント設定ファイルで構築する場合のテストクラス。
 */
public class ThymeleafMailProcessorContainerManagedTest {

    @Rule
    public SystemRepositoryResource systemRepositoryResource = new SystemRepositoryResource(
            "nablarch/integration/mail/thymeleaf/ThymeleafMailProcessorContainerManagedTest.xml");

    /**
     * コンポーネント設定ファイルで構築するテスト。
     */
    @Test
    public void testProcessConfiguredByXml() {

        ThymeleafMailProcessor sut = SystemRepository.get("templateEngineMailProcessor");

        //テンプレートエンジンの処理をして設定済みITemplateEngineを
        //使用できていることを確認する。
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("foo", "0");
        variables.put("bar", false);
        variables.put("bazs", Arrays.asList("1", "2", "3"));
        TemplateEngineProcessedResult result = sut.process(
                "nablarch/integration/mail/thymeleaf/testProcessConfiguredByXml.txt", null,
                Collections.unmodifiableMap(variables));

        assertThat(result.getSubject(), is("あああ0"));
        assertThat(result.getMailBody(), is("いいい\nえええ1\nえええ2\nえええ3\n"));
    }
}
