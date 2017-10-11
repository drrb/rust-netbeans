/**
 * Copyright (C) 2017 drrb
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.completion;

import java.util.List;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.spi.DefaultCompletionResult;

/**
 *
 */
public class RustCompletionResult extends DefaultCompletionResult {

    public enum Truncated {

        YES(true),
        NO(false);
        private final boolean truncated;

        private Truncated(boolean truncated) {
            this.truncated = truncated;
        }
    }

    public RustCompletionResult(List<CompletionProposal> completionProposals, Truncated truncated) {
        super(completionProposals, truncated.truncated);
    }
}
