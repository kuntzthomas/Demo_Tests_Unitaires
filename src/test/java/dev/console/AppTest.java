package dev.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.exception.CalculException;
import dev.service.CalculService;

public class AppTest {

	private static final Logger LOG = LoggerFactory.getLogger(AppTest.class);

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	private App app;

	private CalculService calculService;

	@Rule
	public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

	@Before
	public void setUp() throws Exception {

		Scanner scanner = new Scanner(System.in);
		this.calculService = mock(CalculService.class);
		this.app = new App(scanner, calculService);

	}

	@Test
	public void testAfficherTitre() throws Exception {

		this.app.afficherTitre();

		String logConsole = systemOutRule.getLog();

		assertThat(logConsole).contains("**** Application Calculatrice ****");

	}

	@Test
	public void testEvaluer() throws Exception {

		LOG.info("Etant donné, un service CalculService qui retourne 35 à l'évaluation de l'expression 1+34");

		String expression = "1+34";

		when(calculService.additionner(expression)).thenReturn(35);

		LOG.info("Lorsque la méthode evaluer est invoquée");

		this.app.evaluer(expression);

		LOG.info("Alors le service est invoqué avec l'expression {}", expression);

		verify(calculService).additionner(expression);

		LOG.info("Alors dans la console, s'affiche 1+34=35");

		assertThat(systemOutRule.getLog()).contains("1+34=35");

	}

	@Test(expected = CalculException.class)
	public void testCalculException() throws CalculException {

		LOG.info("Etant donné, un service CalculService qui retourne 35 à l'évaluation de l'expression 1+34++");
		String expression = "1+34++";
		when(calculService.additionner(expression)).thenThrow(CalculException.class);

		LOG.info("Lorsque la méthode evaluer est invoquée");
		this.app.evaluer(expression);

		String logConsole = systemOutRule.getLog();
		assertThat(logConsole).contains("L’expression " + expression + " est invalide.");
	}

	@Test
	public void testDemarrer1() {

		systemInMock.provideLines("fin");
		app.demarrer();
		String logConsole = systemOutRule.getLog();
		assertThat(logConsole).contains("**** Application Calculatrice ****");
		assertThat(logConsole).contains("Aurevoir :-(");
	}

	@Test
	public void testDemarrer2() {

		systemInMock.provideLines("1+2", "fin");
		when(calculService.additionner("1+2")).thenReturn(3);
		this.app.demarrer();

		String logConsole = systemOutRule.getLog();
		assertThat(logConsole).contains("Veuillez saisir une expression");
		assertThat(logConsole).contains("1+2=3");
	}

	@Test
	public void testDemarrer3() {

		systemInMock.provideLines("AAAA", "fin");
		when(calculService.additionner("AAAA")).thenThrow(CalculException.class);
		this.app.demarrer();

		String logConsole = systemOutRule.getLog();
		assertThat(logConsole).contains("L’expression AAAA est invalide.");
	}

	@Test
	public void testDemarrer4() {

		systemInMock.provideLines("1+2", "30+2", "fin");
		when(calculService.additionner("1+2")).thenReturn(3);
		when(calculService.additionner("30+2")).thenReturn(32);
		this.app.demarrer();

		String logConsole = systemOutRule.getLog();
		assertThat(logConsole).contains("1+2=3");
		assertThat(logConsole).contains("30+2=32");
	}
}
