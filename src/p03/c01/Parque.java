package src.p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque {

	private final static int MAX = 20;
	private final static int MIN = 0;

	private int contadorPersonasTotales;
	private int contadorTotalMovimientos;
	private int contadorTotalEntrada;
	private int contadorTotalSalida;
	private Hashtable<String, Integer> contadoresPersonasPuerta;

	public Parque() {

		contadorPersonasTotales = 0;
		contadorTotalMovimientos = 0;
		contadorTotalEntrada = 0;
		contadorTotalSalida = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();

	}

	@Override
	public synchronized void entrarAlParque(String puerta) throws InterruptedException { 
		
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null) {
			contadoresPersonasPuerta.put(puerta, 0);
		}

		comprobarAntesDeEntrar();

		// Aumentamos el contador total y el individual
		contadorTotalMovimientos++;
		contadorPersonasTotales++;
		contadorTotalEntrada++;
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta) + 1);

		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");

		notifyAll();
		checkInvariante();

	}

	@Override
	public synchronized void salirDelParque(String puerta) throws InterruptedException {

		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null) {
			contadoresPersonasPuerta.put(puerta, 0);
		}

		comprobarAntesDeSalir();

		// Aumentamos el contador total y el individual
		contadorTotalMovimientos++;
		contadorPersonasTotales--;
		contadorTotalSalida++;
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta) - 1);

		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Salida");

		notifyAll();
		checkInvariante();

	}

	private void imprimirInfo(String puerta, String movimiento) {
		
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); // + " tiempo medio de estancia: " +

		// Iteramos por todas las puertas e imprimimos sus entradas
		for (String p : contadoresPersonasPuerta.keySet()) {
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}

	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
		Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
		while (iterPuertas.hasMoreElements()) {
			sumaContadoresPuerta += iterPuertas.nextElement();
		}
		return sumaContadoresPuerta;
	}

	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas"
				+ " debe ser igual al valor del contador del parte";
		assert contadorPersonasTotales == contadorTotalSalida + contadorTotalEntrada : "INV: La suma de contadores"
				+ " de las puertas debe ser igual al valor del contador de personas Total";
		for (String p : contadoresPersonasPuerta.keySet()) {
			assert contadoresPersonasPuerta.get(p) < 10 && contadoresPersonasPuerta.get(p) > 0 : "INV: El contador de personas "
					+ "por cada puerta siempre tienen que ser mayor que 0 y menor que su maximo";
		}
	}

	protected void comprobarAntesDeEntrar() throws InterruptedException {
		
		for (String p : contadoresPersonasPuerta.keySet()) {

			while (contadoresPersonasPuerta.get(p) == MAX) {
				wait();
			}
		}	
	}

	protected void comprobarAntesDeSalir() throws InterruptedException {

		for (String p : contadoresPersonasPuerta.keySet()) {

			while (contadoresPersonasPuerta.get(p) == MIN && contadorTotalEntrada != MAX) {
				wait();
			}
		}
	}

}
