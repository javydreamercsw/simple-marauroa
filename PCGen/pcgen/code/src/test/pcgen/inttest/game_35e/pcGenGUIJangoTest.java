package pcgen.inttest.game_35e;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.inttest.pcGenGUITestCase;

/**
 * See PCG file for details. 
 */
@SuppressWarnings("nls")
public class pcGenGUIJangoTest extends pcGenGUITestCase
{

	/**
	 * standard JUnit style constructor
	 * 
	 * @param name 
	 */
	public pcGenGUIJangoTest(String name)
	{
		super(name);
	}

	/**
	 * Return a suite containing all the tests in this class.
	 * 
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		return new TestSuite(pcGenGUIJangoTest.class);
	}

	/**
	 * Load and output the character.
	 * 
	 * @throws Exception If an error occurs.
	 */
	public void testJango() throws Exception
	{
		runTest("35e_test_Jango", "35e", true);
	}
}