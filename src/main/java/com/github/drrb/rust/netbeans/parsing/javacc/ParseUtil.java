package com.github.drrb.rust.netbeans.parsing.javacc;

import org.netbeans.modules.csl.api.OffsetRange;

public class ParseUtil {

    private ParseUtil() {
    }

    public static OffsetRange offsetRange(SimpleNode node) {
        return new OffsetRange(node.jjtGetFirstToken().absoluteBeginPosition - 1, node.jjtGetLastToken().absoluteEndPosition - 1);
    }
}
