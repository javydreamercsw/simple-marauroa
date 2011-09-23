package simple.server.core.account;

import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPObject;
import marauroa.common.game.Result;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

import org.apache.log4j.Logger;
import simple.server.core.engine.SimpleRPObjectFactory;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.engine.SimpleSingletonRepository;

/**
 * Creates a new character as requested by a client.
 */
public class CharacterCreator {

    private static Logger logger = Logger.getLogger(CharacterCreator.class);
    private final ValidatorList validators = new ValidatorList();
    private final String username;
    private final String character;
    private final RPObject template;

    /**
     * create a CharacterCreator.
     *
     * @param username
     *            name of the user
     * @param character
     *            name of the new character
     * @param template
     *            template to base this character on
     */
    public CharacterCreator(final String username, final String character, final RPObject template) {
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

        final TransactionPool transactionPool = SimpleSingletonRepository.getTransactionPool();
        final DBTransaction trans = transactionPool.beginWork();
        final CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);

        try {
            if (characterDAO.hasCharacter(trans, username, character)) {
                logger.warn("Character already exist: " + character);
                return new CharacterResult(Result.FAILED_PLAYER_EXISTS,
                        character, template);
            }

            RPObject object = (RPObject) SimpleRPObjectFactory.createDefaultClientObject(character);
            // monitor new account names
            final String text = "Support: A new character has just been created called " + character + ".";

            SimpleRPRuleProcessor.sendMessageToSupporters(text);

            /*
             * Finally we add it to database.
             */
            characterDAO.addCharacter(trans, username, character, object);
            transactionPool.commit(trans);

            return new CharacterResult(Result.OK_CREATED, character, object);
        } catch (final Exception e) {
            transactionPool.rollback(trans);
            logger.error("Can't create character", e);
            return new CharacterResult(Result.FAILED_EXCEPTION, character, template);
        }
    }
}
