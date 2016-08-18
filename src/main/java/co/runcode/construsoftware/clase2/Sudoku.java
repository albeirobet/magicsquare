package co.runcode.construsoftware.clase2;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class Sudoku extends JFrame {

	private static final long serialVersionUID = 7268127865343454458L;

	private JTextField fields[][];
	private JButton solve;

	public Sudoku() {
		setSize(300, 300);
		launchWidgets();
		launchEvents();
		setVisible(true);

	}

	private void launchWidgets() {
		JPanel panel = new JPanel(new GridLayout(9, 9));
		fields = new JTextField[9][9];
		solve = new JButton("Resolve");
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				fields[r][c] = new JTextField();
				panel.add(fields[r][c]);
			}
		}
		add(panel, BorderLayout.CENTER);
		add(solve, BorderLayout.SOUTH);

	}

	private void launchEvents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		solve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// El objeto solucionador de Choco de Java
				Solver solver = new Solver();
				// Recibe como parametros
				// Nombre
				// Dimensiones de la matriz
				// Dominio de 1 a 9
				IntVar var[][] = VariableFactory.boundedMatrix("v", 9, 9, 1, 9, solver);
				// Ahora creamos las restricciones con la clase de restricciones
				// de enteros de choco
				// Vamos a realizar una matrix espejoç
				IntVar mirror[][] = new IntVar[9][9];
				for (int r = 0; r < 9; r++) {
					for (int c = 0; c < 9; c++) {
						mirror[r][c] = var[c][r];
					}
				}
				for (int r = 0; r < 9; r++) {
					solver.post(ICF.alldifferent(var[r]));
					solver.post(ICF.alldifferent(mirror[r]));
				}

				for (int sr = 0; sr < 3; sr++) {
					for (int sc = 0; sc < 3; sc++) {
						IntVar segment[] = new IntVar[9];
						int i=0;
						for (int r = 0; r < 3; r++) {
							for (int c = 0; c < 3; c++) {
								segment[i]=var[sr*3+r][sc*3+c];
								i++;
							}
						}
						solver.post(ICF.alldifferent(segment));
					}
				}
				
				// Dinamyc Constraints 
				for (int r = 0; r < 9; r++) {
					for (int c = 0; c < 9; c++) {
						if(!fields[r][c].getText().isEmpty()){
							int value = Integer.parseInt(fields[r][c].getText());
							solver.post(ICF.arithm(var[r][c], "=", value));
							fields[r][c].setEnabled(false);
						}
					}
				}
				

				if (solver.findSolution()) {
					for (int r = 0; r < 9; r++) {
						for (int c = 0; c < 9; c++) {
							fields[r][c].setText("" + var[r][c].getValue());
						}
					}
				}
			}
		});
	}

	public static void main(String[] args) {
		new Sudoku();

	}

}
