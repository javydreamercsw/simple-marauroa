package simple.server.core.account;

import marauroa.common.game.Result;

/**
 * Validates the character used for the character name.
 * 
 * @author hendrik
 */
public class NameCharacterValidator implements AccountParameterValidator {

    private String parameterValue;

    /**
     * creates a NameCharacterValidator.
     *
     * @param parameterValue
     *            value to validate
     */
    public NameCharacterValidator(final String parameterValue) {
        this.parameterValue = parameterValue;
    }

    @Override
    public Result validate() {
        // only letters are allowed (and numbers :-/)
        for (int i = parameterValue.length() - 1; i >= 0; i--) {
            char chr = parameterValue.charAt(i);
            if (chr < 'a' || chr > 'z') {
                return Result.FAILED_INVALID_CHARACTER_USED;
            }
        }

        // at lest the first character must be a letter
        char chr = parameterValue.charAt(0);
        if ((chr < 'a' || chr > 'z')) {
            return Result.FAILED_INVALID_CHARACTER_USED;
        }

        return null;
    }
}
