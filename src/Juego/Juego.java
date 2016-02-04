package Juego;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Juego extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	private static final int ANCHO = 800;
	private static final int ALTO = 800;

	private static volatile boolean corriendo = false;

	private static final String NOMBRE = "Ticataquera";

	private static int aps = 0;
	private static int fps = 0;

	private static JFrame ventana;
	private static Thread thread;

	private Juego() {
		setPreferredSize(new Dimension(ANCHO, ALTO));
		ventana = new JFrame(NOMBRE);
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setResizable(false);
		ventana.setLayout(new BorderLayout());
		ventana.add(this, BorderLayout.CENTER);
		ventana.pack();
		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	public static void main(String[] args) {
		Juego juego = new Juego();
		juego.iniciar();
	}

	private synchronized void iniciar() {
		corriendo = true;

		thread = new Thread(this, "Graficos");
		thread.start();
	}

	private synchronized void detener() {
		corriendo = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void actualizar() {
		aps++;
	}

	private void mostrar() {
		fps++;
	}

	public void run() {
		final int NS_POR_SEGUNDO = 1000000000;
		final byte APS_OBJETIVO = 60;
		final double NS_POR_ACTUALIZACION = NS_POR_SEGUNDO / APS_OBJETIVO;

		long referenciaActualizacion = System.nanoTime();
		long referenciaContador = System.nanoTime();

		double tiempoTranscurrido;
		double delta = 0;

		while (corriendo) {
			final long INICIO_BUCLE = System.nanoTime();

			tiempoTranscurrido = INICIO_BUCLE - referenciaActualizacion;
			referenciaActualizacion = INICIO_BUCLE;
			delta += tiempoTranscurrido / NS_POR_ACTUALIZACION;

			while (delta >= 1) {
				actualizar();
				delta--;
			}

			if (System.nanoTime() - referenciaContador > NS_POR_SEGUNDO) {
				ventana.setTitle(NOMBRE + " //  APS: " + aps + " //  FPS: " + fps);
				aps = 0;
				fps = 0;
				referenciaContador = System.nanoTime();
			}

			mostrar();
		}
	}
}