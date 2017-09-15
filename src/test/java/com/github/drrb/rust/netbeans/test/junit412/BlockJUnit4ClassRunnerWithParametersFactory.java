package com.github.drrb.rust.netbeans.test.junit412;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;

/**
 * A {@link ParametersRunnerFactory} that creates
 * {@link BlockJUnit4ClassRunnerWithParameters}.
 *
 * @since 4.12
 */
public class BlockJUnit4ClassRunnerWithParametersFactory implements
        ParametersRunnerFactory {
    public Runner createRunnerForTestWithParameters(TestWithParameters test)
            throws InitializationError {
        return new BlockJUnit4ClassRunnerWithParameters(test);
    }
}
