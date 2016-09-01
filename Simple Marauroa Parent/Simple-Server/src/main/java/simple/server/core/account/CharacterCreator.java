package simple.server.core.account;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPObject;
import marauroa.common.game.Result;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;
import org.openide.util.Lookup;
import simple.server.core.engine.IRPObjectFactory;
import simple.server.core.engine.SimpleRPRuleProcessor;

/**
 * Creates a new character as requested by a client.
 */
public class CharacterCreator {

    private static final Logger LOG
            = Logger.getLogger(CharacterCreator.class.getSimpleName());
    private final ValidatorList validators = new ValidatorList();
    private final String username;
    private final String character;
    private final RPObject template;

    /**
     * create a CharacterCreator.
     *
     * @param username name of the user
     * @param character name of the new character
     * @param template template to base this character on
     */
    public CharacterCreator(final String username, final String character,
            final RPObject template) {
        this.username = username;
        this.character = character;
        this.template = template;
        setupValidatorsForCharacter();
    }

    private void setupValidatorsForCharacter() {
        validators.add(new NotEmptyValidator(character));
        validators.add(new MinLengthValidator(character, 4));
        validators.add(new MaxLengthValidator(character, 20));
        validators.add(new LowerCaseValidator(character));
        validators.add(new NameCharacterValidator(character));
        validators.add(new ReservedSubStringValidator(character));
    }

    /**
     * tries to create this character.
     *
     * @return CharacterResult
     */
    public CharacterResult create() {
        final Result result = validators.runValidators();
        if (result != null) {
            return new CharacterResult(result, character, template);
        }

        final TransactionPool transactionPool
                = TransactionPool.get();
        final DBTransaction trans = transactionPool.beginWork();
        final CharacterDAO characterDAO
                = DAORegister.get().get(CharacterDAO.class);

        try {
            if (characterDAO.hasCharacter(trans, username, character)) {
                LOG.log(Level.WARNING, "Character already exist: {0}",
                        character);
                return new CharacterResult(Result.FAILED_PLAYER_EXISTS,
                        character, template);
            }

            RPObject object
                    = (RPObject) Lookup.getDefault()
                    .lookup(IRPObjectFactory.class)
                    .createDefaultClientObject(character);
            // monitor new account names
            final String text = "Support: A new character has just been "
                    + "created called " + character + ".";

            SimpleRPRuleProcessor.sendMessageToSupporters(text);

            /*
             * Finally we add it to database.
             */
            characterDAO.addCharacter(trans, username, character, object);
            transactionPool.commit(trans);

            return new CharacterResult(Result.OK_CREATED, character, object);
        }
        catch (final SQLException | IOException e) {
            transactionPool.rollback(trans);
            LOG.log(Level.SEVERE, "Can't create character", e);
            return new CharacterResult(Result.FAILED_EXCEPTION, character,
                    template);
        }
    }
}
