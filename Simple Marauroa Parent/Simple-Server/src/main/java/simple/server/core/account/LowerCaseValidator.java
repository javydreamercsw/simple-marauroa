package simple.server.core.account;

import marauroa.common.game.Result;

/**
 * Checks that only lower case letters are used.
 * 
 * @author hendrik
 */
public class LowerCaseValidator implements AccountParameterValidator {

    private String parameterValue;

    /**
     * creates a LowerCaseValidator.
     *
     * @param parameterValue
     *            value to validate
     */
    public LowerCaseValidator(final String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public Result validate() {
        if (!parameterValue.toLowerCase().equals(parameterValue)) {
            return Result.FAILED_INVALID_CHARACTER_USED;
        }

        return null;
    }
}
