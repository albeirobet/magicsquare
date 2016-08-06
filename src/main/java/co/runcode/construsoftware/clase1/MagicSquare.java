package co.runcode.construsoftware.clase1;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.SynchronousQueue;

import javax.print.attribute.standard.JobKOctetsProcessed;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class MagicSquare extends JFrame {

	private static final long serialVersionUID = 6709396266853307836L;
	private JTextField n[];
	private JButton test, solve;
	private int m;

	public MagicSquare() {
		setTitle("Magic Square");
		setSize(400, 400);
		launchWigets();
		launchEvents();
		setVisible(true);

	}

	public static void main(String[] args) {
		new MagicSquare();
	}

	private void launchWigets() {
		test = new JButton("Test");
		solve = new JButton("Solve");
		JPanel panel = new JPanel(new GridLayout(3, 3));
		add(test, BorderLayout.NORTH);
		add(solve, BorderLayout.SOUTH);
		add(panel, BorderLayout.CENTER);
		n = new JTextField[9];
		for (int i = 0; i < 9; i++) {
			n[i] = new JTextField();
			panel.add(n[i]);
		}
	}

	private void launchEvents() {
		m = (3 * (3 * 3 + 1)) / 2;
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		test.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int x1 = 0;
				int x2 = 0;
				int x3 = 0;
				int y1 = 0;
				int y2 = 0;
				int y3 = 0;
				int diag1 = 0;
				int diag2 = 0;

				for (int i = 0; i < 9; i++) {
					// System.out.println(" >> "+n[i].getText());
					if (i < 3) {
						x1 += Integer.parseInt(n[i].getText());
					}
					if (i > 2 && i < 6) {
						// System.out.println(" Sumando y " + n[i].getText());
						x2 += Integer.parseInt(n[i].getText());
					}
					if (i > 5 && i < 9) {
						// System.out.println(" Sumando z " + n[i].getText());
						x3 += Integer.parseInt(n[i].getText());
					}

					if ((i % 2) == 0) {
						// System.out.println(" Sumando Mod " + n[i].getText());
					}

					if (i == 0 || i == 3 || i == 6) {
						// System.out.println(" Sumando Ver 1 " +
						// n[i].getText());
						y1 += Integer.parseInt(n[i].getText());
					}
					if (i == 1 || i == 4 || i == 7) {
						// System.out.println(" Sumando Ver 2 " +
						// n[i].getText());
						y2 += Integer.parseInt(n[i].getText());
					}

					if (i == 2 || i == 5 || i == 8) {
						// System.out.println(" Sumando Ver 3 " +
						// n[i].getText());
						y3 += Integer.parseInt(n[i].getText());
					}

					if (i == 0 || i == 4 || i == 8) {
						// System.out.println(" Sumando Ver 3 " +
						// n[i].getText());
						diag1 += Integer.parseInt(n[i].getText());
					}

					if (i == 2 || i == 4 || i == 6) {
						// System.out.println(" Sumando Ver 3 " +
						// n[i].getText());
						diag2 += Integer.parseInt(n[i].getText());
					}

				}
				//
				System.out.println(" Suma x1  " + x1);
				System.out.println(" Suma x2  " + x2);
				System.out.println(" Suma x3  " + x3);
				System.out.println(" Suma y1  " + y1);
				System.out.println(" Suma y2  " + y2);
				System.out.println(" Suma y3  " + y3);
				System.out.println(" Suma diag1  " + diag1);
				System.out.println(" Suma diag2  " + diag2);

				System.out.println(" Este es el valor de M  " + m);
				if (x1 == m && x2 == m && x3 == m && y1 == m && y2 == m && y3 == m && diag1 == m && diag2 == m) {
					System.out.println(" Los datos digitados solucionan el Cuadro Mágico Don Pingo");
					JOptionPane.showMessageDialog(null, "Los datos digitados solucionan el Cuadro Mágico");

				} else {
					System.out.println(" Los datos digitados son Incorrectos");
					JOptionPane.showMessageDialog(null, "Los datos digitados son Incorrectos");
				}

			}
		});

		solve.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Solver solver = new Solver();
				IntVar v[] = VariableFactory.boundedArray("", 9, 1, m, solver);
				solver.post(ICF.alldifferent(v));
				IntVar M = VariableFactory.fixed(m, solver);
				IntVar vm1[][] = new IntVar[3][3];
				IntVar vm2[][] = new IntVar[3][3];
				
				
				int x = 0;
				for (int r = 0; r < 3; r++) {
					for (int c = 0; c < 3; c++) {
						vm1[r][c] = v[x];
						vm2[c][r] = v[x];
						x++;
					}
					solver.post(ICF.sum(vm1[r], M));
				}
				
				
				IntVar diag1[]= new IntVar[3];
				IntVar diag2[]= new IntVar[3];
				for(int d=0;d<3;d++){
					diag1[d]= vm1[d][d];
					diag2[d]= vm1[d][2-d];
				}
				
				solver.post(ICF.sum(diag1, M));
				solver.post(ICF.sum(diag2, M));
				for (int c = 0; c < 3; c++) {
					solver.post(ICF.sum(vm2[c], M));
				}

				solver.findSolution();
				for (int i = 0; i < 9; i++) {
					n[i].setText("" + v[i].getValue());
				}
			}
		});

	}

}
