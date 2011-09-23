package simple.client.sound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class SoundSystemTest {

    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

        /**
         *
         * @throws java.lang.Exception
         */
        @AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


        /**
         *
         */
        @Test
	public final void testContains() {
		assertFalse(SoundSystem.get().contains(""));
	}

        /**
         *
         */
        @Test
	public final void testSetandGetMute() {
		SoundSystem.get().setMute(true);
		assertTrue("muted sound should be mute", SoundSystem.get().isMute());
		SoundSystem.get().setMute(false);
		assertFalse("muted sound should be mute", SoundSystem.get().isMute());
	}

        /**
         *
         */
        @Test
	public final void testSetAndGetVolume() {
		SoundSystem.get().setVolume(0);
		assertEquals(0, SoundSystem.get().getVolume());
		SoundSystem.get().setVolume(100);
		assertEquals(100, SoundSystem.get().getVolume());
	}

        /**
         *
         */
        @Test
	public final void testSetVolumeOutOfBounds() {
		SoundSystem.get().setVolume(-1);
		assertEquals(0, SoundSystem.get().getVolume());
		SoundSystem.get().setVolume(101);
		assertEquals(100, SoundSystem.get().getVolume());
	}

        /**
         *
         */
        @Test
	public final void testIsOperative() {
		assertTrue(SoundSystem.get().isOperative());
	}

        /**
         *
         */
        @Test
	public final void testGet() {
		final SoundSystem ss1 = SoundSystem.get();
		final SoundSystem ss2 = SoundSystem.get();
		assertTrue("must receive identical instance", (ss1 == ss2));
	}

        /**
         *
         */
        @Test
	public final void testisValidEntry() {

		assertFalse("value has comma x", SoundSystem.get().isValidEntry(",x",
				""));
		assertFalse("key does not start with sfx name has point", SoundSystem
				.get().isValidEntry("", "."));
		assertTrue("key does  start with sfx name has point", SoundSystem.get()
				.isValidEntry("sfx.", "."));
		assertTrue("value has comma x and name has point", SoundSystem.get()
				.isValidEntry("sfx.,x", "."));
	}

}
