package com.github.drrb.rust.netbeans.parsing.javacc;

import com.github.drrb.rust.netbeans.test.junit412.Parameterized;
import com.github.drrb.rust.netbeans.test.junit412.Parameterized.Parameters;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TokenizationTest {

    @Parameters(name = "{0}")
    public static Iterable<TestSrc> sources() throws IOException {
        return TestSrc.all().sorted().collect(toList());
    }

    private final TestSrc sourceFile;

    public TokenizationTest(TestSrc sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Test
    public void testLex() throws Exception {
        ParseResult actualResult = sourceFile.tokenize();
        ExpectedTokensFile expectedTokensFile = sourceFile.expectedTokensFile();
        if (!expectedTokensFile.exists()) {
            Predicate<ParseResult.Token> isGarbage = ParseResult.Token::isGarbage;
            List<ParseResult.Token> garbageTokens = actualResult.tokens.stream().filter(isGarbage).collect(toList());
            if (garbageTokens.isEmpty()) {
                expectedTokensFile.createWith(actualResult);
                fail("Expected tokens file doesn't exist: " + expectedTokensFile + ". Creating it.");
            } else {
                fail("Found garbage tokens: " + garbageTokens + "\nin tokenization result:\n" + actualResult);
            }
        }
        ParseResult expectedResult = expectedTokensFile.tokens();
        if (expectedResult.equals(actualResult)) {
            return;
        }
        throw new ComparisonFailure(
                "Tokenizing " + sourceFile + " didn't produce expected tokens",
                expectedResult.json(),
                actualResult.json()
        );
    }
}
