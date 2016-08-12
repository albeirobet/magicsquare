package co.runcode.construsoftware.clase1;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class MagicSquare extends JFrame {

	private static final long serialVersionUID = 6709396266853307836L;
	private JTextField n[];
	private JButton test, solve;
	private int m;
	private int size;
	private int sizeConvert;

	public MagicSquare() {
		System.out.println(" Inicializando el MagicSquare");
		String sizeString;
		sizeString = JOptionPane.showInputDialog("Digite numero de lados deseados para el Cuadro Màgico");
		if(isInteger(sizeString)){
		size = Integer.parseInt(sizeString);
		sizeConvert = size * size;
		setTitle("Magic Square");
		setSize(400, 400);
		launchWigets();
		launchEvents();
		setVisible(true);
		}else{
			JOptionPane.showMessageDialog(null, "Nùmero digitado es incorrecto!");
		}

	}

	public static void main(String[] args) {
		new MagicSquare();
	}

	private void launchWigets() {

		test = new JButton("Test");
		solve = new JButton("Solve");
		JPanel panel = new JPanel(new GridLayout(size, size));
		add(test, BorderLayout.NORTH);
		add(solve, BorderLayout.SOUTH);
		add(panel, BorderLayout.CENTER);
		n = new JTextField[sizeConvert];
		for (int i = 0; i < sizeConvert; i++) {
			n[i] = new JTextField();
			panel.add(n[i]);
		}
	}

	private void launchEvents() {
		m = (size * (size * size + 1)) / 2;
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		test.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (validateDataMatrix(n)) {
					int matrizTest[][] = new int[size][size];
					int checkFile = size;
					int fila = 0;
					int columna = 0;
					int factorMulti = 1;
					for (int i = 0; i < sizeConvert; i++) {
						// System.out.print(" "+n[i].getText());
						if (i < checkFile) {
							System.out.print(" (" + fila + "," + columna + ") " + n[i].getText());
							matrizTest[fila][columna] = Integer.parseInt(n[i].getText());
							columna++;
						} else {
							factorMulti++;
							System.out.print("\n  ");
							fila++;
							columna = 0;
							System.out.print(" (" + fila + "," + columna + ") " + n[i].getText());
							matrizTest[fila][columna] = Integer.parseInt(n[i].getText());
							columna++;
							checkFile = size * factorMulti;
						}
					}
					System.out.print("\n\n  ");
					for (int[] row : matrizTest)
						System.out.println(Arrays.toString(row));

					boolean checkResult = false;

					int sumaFilas = 0;
					int sumaColumnas = 0;

					for (int r = 0; r < size; r++) {
						for (int c = 0; c < size; c++) {
							sumaFilas += matrizTest[r][c];
						}
						if (sumaFilas == m) {
							checkResult = true;
						} else {
							checkResult = false;
						}
						System.out.println(" Suma fil " + r + "  " + sumaFilas);

						sumaFilas = 0;
					}

					for (int r = 0; r < size; r++) {
						for (int c = 0; c < size; c++) {
							sumaColumnas += matrizTest[c][r];
						}
						System.out.println(" Suma Col " + r + "  " + sumaColumnas);
						if (sumaColumnas == m) {
							checkResult = true;
						} else {
							checkResult = false;
						}
						sumaColumnas = 0;
					}

					int sumDiag1 = 0;
					int sumDiag2 = 0;

					for (int d = 0; d < size; d++) {
						sumDiag1 += matrizTest[d][d];
						sumDiag2 += matrizTest[d][(size - 1) - d];
					}

					if (sumDiag1 == m & sumDiag2 == m) {
						checkResult = true;
					} else {
						checkResult = false;
					}
					System.out.println(" Diag1: " + sumDiag1 + "  Diag2: " + sumDiag2);

					System.out.println(" Este es el valor de M  " + m);
					if (checkResult) {
						System.out.println(" Los datos digitados solucionan el Cuadro Mágico Don Pingo");
						JOptionPane.showMessageDialog(null, "Los datos digitados solucionan el Cuadro Mágico");

					} else {
						System.out.println(" Los datos digitados son Incorrectos");
						JOptionPane.showMessageDialog(null, "Los datos digitados son Incorrectos");
					}
				} else {
					System.out.println(" Los datos digitados son Incorrectos");
					JOptionPane.showMessageDialog(null, "Por favor diligencie todos los datos de manera correcta");
				}

			}
		});

		solve.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Solver solver = new Solver();
				IntVar v[] = VariableFactory.boundedArray("", sizeConvert, 1, m, solver);
				solver.post(ICF.alldifferent(v));
				IntVar M = VariableFactory.fixed(m, solver);
				IntVar vm1[][] = new IntVar[size][size];
				IntVar vm2[][] = new IntVar[size][size];

				int x = 0;
				for (int r = 0; r < size; r++) {
					for (int c = 0; c < size; c++) {
						vm1[r][c] = v[x];
						vm2[c][r] = v[x];
						x++;
					}
					solver.post(ICF.sum(vm1[r], M));
				}

				IntVar diag1[] = new IntVar[size];
				IntVar diag2[] = new IntVar[size];
				for (int d = 0; d < size; d++) {
					diag1[d] = vm1[d][d];
					diag2[d] = vm1[d][(size - 1) - d];
				}

				solver.post(ICF.sum(diag1, M));
				solver.post(ICF.sum(diag2, M));
				for (int c = 0; c < size; c++) {
					solver.post(ICF.sum(vm2[c], M));
				}
				System.out.println(" Entrando a obtener la soluciòn a Choko");
				solver.findSolution();
				System.out.println(" Saliendo de la soluciòn de Choko");
				for (int i = 0; i < sizeConvert; i++) {
					n[i].setText("" + v[i].getValue());
				}
			}
		});

	}

	public boolean validateDataMatrix(JTextField n[]) {
		boolean flag = false;
		if (n.length == sizeConvert) {
			for (int i = 0; i < n.length; i++) {
				flag = isInteger(n[i].getText());
				if (!flag) {
					i = n.length;
				}
			}
		}

		return flag;
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

}
