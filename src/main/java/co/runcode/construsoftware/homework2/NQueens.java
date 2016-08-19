package co.runcode.construsoftware.homework2;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class NQueens extends JFrame implements Serializable {

	private static final long serialVersionUID = -7450909058876890587L;
	private JTextField fields[][];
	private JButton test, solve;
	private int size;
	private int sizeConvert;

	public NQueens() {
		size = 30;
		setSize(500, 500);
		launchWidgets();
		launchEvents();
		setVisible(true);
		
	}

	private void launchWidgets() {

		test = new JButton("Test");
		solve = new JButton("Solve");
		JPanel panel = new JPanel(new GridLayout(size, size));
		fields = new JTextField[size][size];
		add(test, BorderLayout.NORTH);
		add(solve, BorderLayout.SOUTH);
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				fields[r][c] = new JTextField();
				// fields[r][c].setText(String.valueOf(contador));
				// contador++;
				panel.add(fields[r][c]);
			}
		}
		add(panel, BorderLayout.CENTER);
	}

	private void launchEvents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (validateDataMatrix(fields)) {
					System.out.println(" La matrix esta correcta voy a revisarla");
					int matrixCopy[][] = copiarMatrix(fields);

					for (int[] row : matrixCopy)
						System.out.println(Arrays.toString(row));

					if (checkSumRows(matrixCopy)) {
						if (checkSumCols(matrixCopy)) {
							if (checkSumDiagIzqDer(matrixCopy)) {
								if (checkSumDiagDerIzq(matrixCopy)) {
									System.out.println(" Organizò las "+size+" Reinas Correctamente");
									JOptionPane.showMessageDialog(null, "Organizò las "+size+" Reinas Correctamente");
								} else {
									JOptionPane.showMessageDialog(null,
											"Error al ubicar en las Diagonales de Der to Izq");
									System.out.println(" Error al ubicar en las Diagonales de Der to Izq");
								}
							} else {
								JOptionPane.showMessageDialog(null, "Error al ubicar en las Diagonales Izq to Der");
								System.out.println("Error al ubicar en las Diagonales Izq to Der");
							}
						} else {
							JOptionPane.showMessageDialog(null, "Error al ubicar en las Columnas");
							System.out.println(" Error al ubicar en las Columnas");
						}
					} else {
						JOptionPane.showMessageDialog(null, "Error al Ubicar en las filas");
						System.out.println("Error al Ubicar en las filas");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Por favor diligencie los campos de Manera Correcta.");
					System.out.println(" No vale la pena revisar la matrix");
				}
			}
		});

		solve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Solver solver = new Solver();
				IntVar var[][] = VariableFactory.boundedMatrix("v", size, size, 0, 1, solver);
				IntVar M = VariableFactory.fixed(1, solver);

				IntVar vm1[][] = new IntVar[size][size];
				IntVar vm2[][] = new IntVar[size][size];
				for (int r = 0; r < size; r++) {
					for (int c = 0; c < size; c++) {
						vm1[r][c] = var[r][c];
						vm2[c][r] = var[r][c];
					}
					 solver.post(ICF.sum(vm1[r], M));
				}

				for (int c = 0; c < size; c++) {
					 solver.post(ICF.sum(vm2[c], M));
				}

				// Generando Vector dinamico diagonales Izquierda a Derecha
				 List<IntVar[]> listDiagIzqDer = getVectorDiagonalIzqDer(var);
				 for (IntVar[] diagonal : listDiagIzqDer) {
				// solver.post(ICF.sum(diagonal,M));
					 solver.post(ICF.sum(diagonal, "<=",M));
				 }
				// Generando Vector dinamico diagonales Derecha a Izquierda
				 List<IntVar[]> listDiagDerIzq = getVectorDiagonalDerIzq(var);
				 for (IntVar[] diagonalD : listDiagDerIzq) {
					 solver.post(ICF.sum(diagonalD, "<=",M));
				}

				System.out.println(" Entrando a obtener la soluciòn a Choko");
				solver.findSolution();
				System.out.println(" Saliendo de la soluciòn de Choko");
				if (solver.findSolution()) {
					for (int r = 0; r < size; r++) {
						for (int c = 0; c < size; c++) {
							if (var[r][c].getValue() == 0) {
								fields[r][c].setText("");
							} else {
								fields[r][c].setText("" + var[r][c].getValue());
							}

						}
					}
				}

			}
		});

	}

	public List<IntVar[]> getVectorDiagonalIzqDer(IntVar var[][]) {
		IntVar diag1[] = new IntVar[size];
		List<IntVar[]> listDiag = new ArrayList<IntVar[]>();

		int dim = size;
		System.out.println();
		int contArray = 0;

		for (IntVar[] row : var)
			System.out.println(Arrays.toString(row));

		for (int k = 0; k < dim * 2; k++) {
			for (int j = 0; j <= k; j++) {
				int i = k - j;
				if (i < dim && j < dim) {
					diag1[j] = var[i][j];
				}
			}
			for (int i = 0; i < diag1.length; i++) {
				if (diag1[i] != null) {
					contArray++;
				}
			}
			IntVar diagTmp[] = new IntVar[contArray];
			contArray = 0;
			int posInser = 0;
			for (int i = 0; i < diag1.length; i++) {
				if (diag1[i] != null) {
					diagTmp[posInser] = diag1[i];
					posInser++;
				}
			}
			if (diagTmp.length > 0) {
				listDiag.add(diagTmp);
			}
			diag1 = new IntVar[size];
		}
		return listDiag;
	}

	public List<IntVar[]> getVectorDiagonalDerIzq(IntVar var[][]) {
		IntVar diag1[] = new IntVar[size];
		List<IntVar[]> listDiag = new ArrayList<IntVar[]>();
		int contArray = 0;

		// boolean flag = false;
		// int sumaDiagDerIzq = 0;

		for (int j = size - 1; j >= 0; j--) {
			for (int k = 0; k < size; k++) {
				if ((j + k) < size) {
					// System.out.print( var[k][j + k] + " ");
					diag1[k] = var[k][j + k];
				} else {
					k = size;
				}
			}
			for (int i = 0; i < diag1.length; i++) {
				if (diag1[i] != null) {
					contArray++;
				}
			}
			//System.out.println(Arrays.toString(diag1));
			IntVar diagTmp[] = new IntVar[contArray];
			contArray = 0;
			int posInser = 0;
			for (int i = 0; i < diag1.length; i++) {
				if (diag1[i] != null) {
					diagTmp[posInser] = diag1[i];
					posInser++;
				}
			}
			if (diagTmp.length > 0) {
				listDiag.add(diagTmp);
			}
			diag1 = new IntVar[size];
		}
	

		for (int i = 1; i < size; i++) {
			for (int j = i, k = 0; j < size && k < size; j++, k++) {
				//System.out.print(matrixCopy[j][k] + " ");
			//	sumaDiagDerIzq += matrixCopy[j][k];
				diag1[k] = var[j][k];
			}
			for (int x = 0; x < diag1.length; x++) {
				if (diag1[x] != null) {
					contArray++;
				}
			}
			
			//System.out.println(Arrays.toString(diag1));
			IntVar diagTmp[] = new IntVar[contArray];
			contArray = 0;
			int posInser = 0;
			for (int m = 0; m < diag1.length; m++) {
				if (diag1[m] != null) {
					diagTmp[posInser] = diag1[m];
					posInser++;
				}
			}
			if (diagTmp.length > 0) {
				listDiag.add(diagTmp);
			}
			diag1 = new IntVar[size];
		}

		for (IntVar[] intVars : listDiag) {
			System.out.println(Arrays.toString(intVars));
		}
		return listDiag;

	}

	public boolean validateDataMatrix(JTextField fields[][]) {
		boolean flag = false;
		int contadorReinas = 0;
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (isInteger(fields[r][c].getText())) {
					if (Integer.parseInt(fields[r][c].getText()) == 1) {
						System.out.println(" Entrando a revisar ");
						contadorReinas += 1;
					} else {
						if (!fields[r][c].getText().equals("")) {
							contadorReinas = 0;
							c = size;
						}
					}
				} else {
					if (!fields[r][c].getText().equals("")) {
						contadorReinas = 0;
						c = size;
					}
				}
			}
		}
		System.out.println(" El contador de reinas  " + contadorReinas);
		if (contadorReinas == size) {
			flag = true;
		}
		return flag;
	}

	public boolean checkSumRows(int[][] matrixCopy) {
		boolean flag = false;
		int sumaFilas = 0;
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				sumaFilas += matrixCopy[r][c];
			}
			System.out.println(" Suma fil " + r + "  " + sumaFilas);
			if (sumaFilas == 1) {
				flag = true;
			} else {
				r = size;
				flag = false;
			}
			sumaFilas = 0;
		}
		return flag;
	}

	public boolean checkSumCols(int[][] matrixCopy) {
		boolean flag = false;
		int sumaColumnas = 0;
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				sumaColumnas += matrixCopy[c][r];
			}
			System.out.println(" Suma Col " + r + "  " + sumaColumnas);
			if (sumaColumnas == 1) {
				flag = true;
			} else {
				r = size;
				flag = false;
			}
			sumaColumnas = 0;
		}
		return flag;

	}

	public boolean checkSumDiagIzqDer(int[][] matrixCopy) {
		boolean flag = false;
		int dim = size;
		System.out.println();
		int sumaDiagIzqDer = 0;

		for (int k = 0; k < dim * 2; k++) {
			for (int j = 0; j <= k; j++) {
				int i = k - j;
				if (i < dim && j < dim) {
					System.out.print(matrixCopy[i][j] + " ");
					sumaDiagIzqDer += matrixCopy[i][j];
				}
			}
			System.out.println(" sumaDiagIzqDer " + k + "  " + sumaDiagIzqDer);
			if (sumaDiagIzqDer != 1 && sumaDiagIzqDer != 0) {
				k = (size*2-1);
				flag = false;
			} else {
				sumaDiagIzqDer = 0;
				flag = true;
			}

			System.out.println();
		}
		return flag;
	}

	public boolean checkSumDiagDerIzq(int[][] matrixCopy) {
		boolean flag = false;
		int sumaDiagDerIzq = 0;

		for (int j = size - 1; j >= 0; j--) {
			for (int k = 0; k < size; k++) {
				if ((j + k) < size) {
					System.out.print(matrixCopy[k][j + k] + " ");
					sumaDiagDerIzq += matrixCopy[k][j + k];
				} else {
					k = size;
				}
			}
			System.out.println(" sumaDiagDerIzq " + j + "  " + sumaDiagDerIzq);
			if (sumaDiagDerIzq != 1 && sumaDiagDerIzq != 0) {
				j = -1;
				flag = false;
			} else {
				sumaDiagDerIzq = 0;
				flag = true;
			}
			System.out.println();
		}

		if (flag) {
			for (int i = 1; i < size; i++) {
				for (int j = i, k = 0; j < size && k < size; j++, k++) {
					System.out.print(matrixCopy[j][k] + " ");
					sumaDiagDerIzq += matrixCopy[j][k];
				}
				System.out.println(" sumaDiagDerIzq " + i + "  " + sumaDiagDerIzq);
				if (sumaDiagDerIzq != 1 && sumaDiagDerIzq != 0) {
					i = size;
					flag = false;
				} else {
					sumaDiagDerIzq = 0;
					flag = true;
				}

				System.out.println();
			}
		}
		return flag;
	}

	public int[][] copiarMatrix(JTextField fields[][]) {
		int matrixCopy[][] = new int[size][size];
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (fields[r][c].getText().equals("")) {
					matrixCopy[r][c] = 0;
				} else {
					matrixCopy[r][c] = 1;
				}
			}
		}
		return matrixCopy;
	}

	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	
	public static void main(String[] args) {
		new NQueens();
	}

}
