/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.buscaminas;

/**
 *
 * @author perea
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Buscaminas - Juego clásico implementado con Swing.
 * Tablero 10x10, distribuido con GridBagLayout / GridBagConstraints.
 */
public class Buscaminas extends JFrame {

    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;
    private static final int MINAS = 15;

    private JButton[][] botones;
    private boolean[][] esMina;
    private boolean[][] descubierta;
    private boolean[][] marcada;
    private int[][] minasAlrededor;

    private boolean juegoTerminado;
    private int casillasDescubiertas;

    private JLabel lblMinas;
    private JLabel lblResultado;
    private JPanel panelTablero;

    public Buscaminas() {
        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setResizable(false);

        GridBagConstraints gbcFrame = new GridBagConstraints();
        gbcFrame.gridx = 0;
        gbcFrame.fill = GridBagConstraints.BOTH;
        gbcFrame.weightx = 1.0;

        // ----- Panel superior -----
        gbcFrame.gridy = 0;
        gbcFrame.weighty = 0.0;
        add(crearPanelSuperior(), gbcFrame);

        // ----- Panel central (tablero) -----
        panelTablero = new JPanel();
        gbcFrame.gridy = 1;
        gbcFrame.weighty = 1.0;
        add(panelTablero, gbcFrame);

        // ----- Panel inferior -----
        gbcFrame.gridy = 2;
        gbcFrame.weighty = 0.0;
        add(crearPanelInferior(), gbcFrame);

        nuevaPartida();

        setSize(560, 680);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(""));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel titulo = new JLabel("BUSCAMINAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titulo, gbc);

        lblMinas = new JLabel("Minas: " + MINAS);
        lblMinas.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(lblMinas, gbc);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JButton btnNuevaPartida = new JButton("Nueva partida");
        btnNuevaPartida.addActionListener(e -> nuevaPartida());
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnNuevaPartida, gbc);

        lblResultado = new JLabel("Buena suerte");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(lblResultado, gbc);

        return panel;
    }

    /** Construye (o reconstruye) el tablero de casillas dentro del panel central. */
    private void construirTablero() {
        panelTablero.removeAll();
        panelTablero.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                JButton boton = new JButton();
                boton.setPreferredSize(new Dimension(45, 45));
                boton.setMargin(new Insets(0, 0, 0, 0));
                boton.setFont(new Font("Arial", Font.BOLD, 14));

                final int f = fila;
                final int c = col;
                boton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        descubrirCasilla(f, c);
                    }
                });

                botones[fila][col] = boton;

                gbc.gridx = col;
                gbc.gridy = fila;
                panelTablero.add(boton, gbc);
            }
        }

        panelTablero.revalidate();
        panelTablero.repaint();
    }

    /** Reinicia todas las estructuras de datos y comienza una partida nueva. */
    private void nuevaPartida() {
        botones = new JButton[FILAS][COLUMNAS];
        esMina = new boolean[FILAS][COLUMNAS];
        descubierta = new boolean[FILAS][COLUMNAS];
        marcada = new boolean[FILAS][COLUMNAS];
        minasAlrededor = new int[FILAS][COLUMNAS];

        juegoTerminado = false;
        casillasDescubiertas = 0;

        construirTablero();
        colocarMinas();
        calcularMinasAlrededor();

        lblMinas.setText("Minas: " + MINAS);
        lblResultado.setText("Buena suerte");
    }

    private void colocarMinas() {
        Random random = new Random();
        int colocadas = 0;
        while (colocadas < MINAS) {
            int fila = random.nextInt(FILAS);
            int col = random.nextInt(COLUMNAS);
            if (!esMina[fila][col]) {
                esMina[fila][col] = true;
                colocadas++;
            }
        }
    }

    private void calcularMinasAlrededor() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                if (esMina[fila][col]) {
                    continue;
                }
                int contador = 0;
                for (int df = -1; df <= 1; df++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (df == 0 && dc == 0) {
                            continue;
                        }
                        int nf = fila + df;
                        int nc = col + dc;
                        if (nf >= 0 && nf < FILAS && nc >= 0 && nc < COLUMNAS && esMina[nf][nc]) {
                            contador++;
                        }
                    }
                }
                minasAlrededor[fila][col] = contador;
            }
        }
    }

    private void descubrirCasilla(int fila, int col) {
        if (juegoTerminado || descubierta[fila][col]) {
            return;
        }

        if (esMina[fila][col]) {
            perderPartida(fila, col);
            return;
        }

        revelarEnCadena(fila, col);

        if (casillasDescubiertas == FILAS * COLUMNAS - MINAS) {
            ganarPartida();
        }
    }

    /** Descubre la casilla y, si no tiene minas alrededor, expande a las adyacentes (relleno en cadena). */
    private void revelarEnCadena(int fila, int col) {
        if (fila < 0 || fila >= FILAS || col < 0 || col >= COLUMNAS) {
            return;
        }
        if (descubierta[fila][col] || esMina[fila][col]) {
            return;
        }

        descubierta[fila][col] = true;
        casillasDescubiertas++;

        JButton boton = botones[fila][col];
        boton.setEnabled(false);
        boton.setBackground(Color.LIGHT_GRAY);
        boton.setOpaque(true);
        boton.setBorderPainted(true);

        int minas = minasAlrededor[fila][col];
        if (minas > 0) {
            boton.setText(String.valueOf(minas));
            boton.setForeground(colorParaNumero(minas));
        } else {
            boton.setText("");
            for (int df = -1; df <= 1; df++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (df == 0 && dc == 0) {
                        continue;
                    }
                    revelarEnCadena(fila + df, col + dc);
                }
            }
        }
    }

    private Color colorParaNumero(int numero) {
        switch (numero) {
            case 1: return Color.BLUE;
            case 2: return new Color(0, 128, 0);
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128);
            default: return Color.BLACK;
        }
    }

    private void perderPartida(int filaClic, int colClic) {
        juegoTerminado = true;
        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                JButton boton = botones[fila][col];
                if (esMina[fila][col]) {
                    boton.setText("*");
                    boton.setBackground(fila == filaClic && col == colClic ? Color.RED : Color.ORANGE);
                    boton.setOpaque(true);
                }
                boton.setEnabled(false);
            }
        }
        lblResultado.setText("¡Has perdido!");
        lblResultado.setForeground(Color.RED);
    }

    private void ganarPartida() {
        juegoTerminado = true;
        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                if (esMina[fila][col]) {
                    JButton boton = botones[fila][col];
                    boton.setText("F");
                    boton.setBackground(Color.GREEN);
                    boton.setOpaque(true);
                }
                botones[fila][col].setEnabled(false);
            }
        }
        lblResultado.setText("¡Has ganado!");
        lblResultado.setForeground(new Color(0, 128, 0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Buscaminas::new);
    }
}
