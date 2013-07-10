package com.github.drrb.rust.netbeans.lexer;

import com.github.drrb.rust.netbeans.RustLexer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;
import org.antlr.v4.runtime.Token;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static com.github.drrb.rust.netbeans.RustLexer.*;
import static com.github.drrb.rust.netbeans.RustParser.*;
import com.github.drrb.rust.netbeans.RustParser;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class RustParserTest {
    @Test
    public void shouldParseFunction() {
        StringBuilder function = new StringBuilder();
        function.append("fn greet(name: str) {\n");
        function.append("    io::println(fmt!(\"Hello, %?\", name));\n");
        function.append("}\n");
        
//        ProgContext program = parse(function).prog();
//        Mod_itemContext item = program.module_contents().mod_item().get(0);
//        System.out.println("item = " + item.getText());
//        System.out.println("functionDeclaration = " + functionDeclaration.getText());
//        assertThat(functionDeclaration.ident().getText(), is("greet"));
//        
//        ArgContext param = functionDeclaration.args().arg();
//        assertThat(param.pat().getText(), is("name"));
//        assertThat(param.ty().getText(), is("str"));
        
//        String printlnStatementText = functionDeclaration.fun_body().block_element(0)
//                .expr_RL()
//                .expr_1RL()
//                .expr_2RL()
//                .expr_3RL()
//                .expr_4RL()
//                .expr_5RL()
//                .expr_6RL()
//                .expr_7RL()
//                .expr_8RL()
//                .expr_9RL()
//                .expr_10RL()
//                .expr_11RL()
//                .expr_12RL()
//                .expr_prefixRL()
//                .expr_dot_or_callRL()
//                .expr_dot_or_call()
//                .expr().getText();
//        assertThat(printlnStatementText, is("println"));
        
    }

    private RustParser parse(CharSequence input) {
        return new RustParser(new UnbufferedTokenStream(new RustLexer(new ANTLRInputStream(input.toString()))));
    }

}
