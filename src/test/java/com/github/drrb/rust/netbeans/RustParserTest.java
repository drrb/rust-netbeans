package com.github.drrb.rust.netbeans;

import com.github.drrb.rust.netbeans.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.NetbeansRustParser.SyntaxError;
import org.junit.Test;
import java.util.Iterator;
import javax.swing.text.Document;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class RustParserTest {

    @Test
    public void shouldFindError() throws Exception {
        StringBuilder function = new StringBuilder();
        function.append("fn greet(name: str) {\n");
        function.append("    xxx io::println(fmt!(\"Hello, %?\", name));\n");
        function.append("}\n");
        
        NetbeansRustParserResult result = parse(function);
        
        Iterator<SyntaxError> syntaxErrors = result.getSyntaxErrors().iterator();
        SyntaxError syntaxError = syntaxErrors.next();
        assertThat(syntaxError.getLine(), is(2));
        assertThat(syntaxError.getCharPositionInLine(), is(8));
        assertThat(syntaxError.getMessage(), is("no viable alternative at input 'xxx io'"));
    }

    private NetbeansRustParserResult parse(CharSequence input) throws Exception {
        Document document = RustDocument.containing(input);
        Source source = Source.create(document);
        Snapshot snapshot = source.createSnapshot();
        NetbeansRustParser parser = new NetbeansRustParser();
        parser.parse(snapshot, null, null);
        return parser.getResult(null);
    }
}
