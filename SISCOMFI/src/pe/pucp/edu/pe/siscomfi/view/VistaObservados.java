package pe.pucp.edu.pe.siscomfi.view;

import javax.swing.JInternalFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import pe.pucp.edu.pe.siscomfi.bm.BD.siscomfiManager;
import pe.pucp.edu.pe.siscomfi.model.Adherente;
import pe.pucp.edu.pe.siscomfi.model.PartidoPolitico;
import pe.pucp.edu.pe.siscomfi.model.Proceso;

import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import java.awt.Color;

@SuppressWarnings("serial")
public class VistaObservados extends JInternalFrame implements ActionListener {
	private JTextField txtCantObservados;
	private JTable tblAdherentes;
	private JTextField txtAdherente;
	private JTextField txtCodigoPlanillon;
	private JComboBox<String> cmbPartido;
	private MyTableModel tableModelAdherentes;
	private JButton btnAceptado;
	private JButton btnRechazado;
	private JButton btnSalir;
	private JTextField txtAceptados;
	private JPanel pnOriginalH;
	private JPanel pnObservadoH;
	private JPanel pnOriginalF;
	private JPanel pnObservadoF;
	private JLabel lblHuellaNoDisponible;
	private JLabel lblHuellaOriginal;
	private JLabel lblHuellaObservado;
	private JLabel lblFirmaNoDisponible;
	private JLabel lblFirmaOriginal;
	private JLabel lblFirmaObservado;
	private JTextField txtDni;
	private JButton btnTerminar;
	private int filaSeleccionada;
	private String pathObservados = "C:\\Users\\samoel\\Desktop\\Real\\SISCOMFI-Vistas\\SISCOMFI\\Proceso\\Observados";
	private Proceso fase;
	private File pObservadosPartido;
	
	public VistaObservados() {
		setClosable(true);
		setTitle("Adherentes observados");
		setBounds(100, 100, 900, 456);
		getContentPane().setLayout(null);

		// Fase del Proceso
		fase = siscomfiManager.getFase1Proceso();
		if (fase == null) {
			fase = siscomfiManager.getFase2Proceso();
			if (fase == null) {
				JOptionPane.showMessageDialog(this, "No hay procesos electorales activos");
				this.dispose();
			}
		}

		btnSalir = new JButton("Salir");
		btnSalir.setBounds(289, 393, 113, 23);
		getContentPane().add(btnSalir);

		JPanel pnPartido = new JPanel();
		pnPartido.setLayout(null);
		pnPartido.setBorder(
				new TitledBorder(null, "PARTIDO POLITICO", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnPartido.setBounds(10, 11, 396, 114);
		getContentPane().add(pnPartido);

		JLabel lblTotalObservados = new JLabel("Total observados:");
		lblTotalObservados.setBounds(10, 49, 151, 14);
		pnPartido.add(lblTotalObservados);

		JLabel lblPartidoPolitico = new JLabel("Partido politico:");
		lblPartidoPolitico.setBounds(10, 24, 151, 14);
		pnPartido.add(lblPartidoPolitico);

		txtCantObservados = new JTextField();
		txtCantObservados.setEditable(false);
		txtCantObservados.setColumns(10);
		txtCantObservados.setBounds(171, 46, 208, 20);
		pnPartido.add(txtCantObservados);

		cmbPartido = new JComboBox<String>();
		cmbPartido.setBounds(171, 21, 208, 20);
		// llenar el partido
		fillCmbPartido();
		cmbPartido.addItem("1 - PPK");
		cmbPartido.addItem("2 - APRA");
		pnPartido.add(cmbPartido);

		JLabel lblAceptados = new JLabel("Total Aceptados:");
		lblAceptados.setBounds(10, 74, 151, 14);
		pnPartido.add(lblAceptados);

		txtAceptados = new JTextField();
		txtAceptados.setEditable(false);
		txtAceptados.setText("0");
		txtAceptados.setColumns(10);
		txtAceptados.setBounds(171, 71, 208, 20);
		pnPartido.add(txtAceptados);

		JPanel pnObservados = new JPanel();
		pnObservados.setBounds(10, 130, 395, 252);
		getContentPane().add(pnObservados);
		pnObservados.setLayout(new GridLayout(1, 0, 0, 0));

		JScrollPane scrollPane = new JScrollPane();
		pnObservados.add(scrollPane);

		// Table
		tblAdherentes = new JTable();
		tblAdherentes.getTableHeader().setReorderingAllowed(false);
		tableModelAdherentes = new MyTableModel();
		tblAdherentes.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = tblAdherentes.getSelectedRow();
				filaSeleccionada = row;
				String dni = tblAdherentes.getValueAt(row, 0).toString();
				String nombre = tblAdherentes.getValueAt(row, 1).toString();
				String apPaterno = tblAdherentes.getValueAt(row, 2).toString();
				String apMaterno = tblAdherentes.getValueAt(row, 3).toString();
				String nCompleto = nombre + " " + apPaterno + " " + apMaterno;
				txtDni.setText(dni);
				txtAdherente.setText(nCompleto);

				// Cargamos las imagenes en los panel
				
				File[] dniObservados = pObservadosPartido.listFiles();
				File dniEncontrado = null;
				for (File obs : dniObservados) {
					if (obs.getName().compareTo(dni) == 0) {
						dniEncontrado = obs;
						break;
					}
				}
				if (dniEncontrado != null) {
					File pathHuella = new File(dniEncontrado.getAbsolutePath() + "\\huella");
					if (pathHuella.exists()) {
						File[] huellas = pathHuella.listFiles();
						for (File huella : huellas) {
							if (huella.getName().compareTo("original.jpg") == 0) {
								ImagePlus iHuellaOriginal = IJ.openImage(huella.getAbsolutePath());
								int width = lblHuellaOriginal.getWidth();
								ImageProcessor imp = iHuellaOriginal.getProcessor();
								imp = imp.resize(width);
								iHuellaOriginal = new ImagePlus("original", imp);
								lblHuellaOriginal.setBounds(lblHuellaObservado.getBounds().x,
										lblHuellaObservado.getBounds().y, iHuellaOriginal.getWidth(),
										iHuellaOriginal.getHeight());
								lblHuellaOriginal.setIcon(new ImageIcon(iHuellaOriginal.getBufferedImage()));
								pnOriginalH.setBounds(pnOriginalH.getBounds().x, pnOriginalH.getBounds().y,
										iHuellaOriginal.getWidth(), iHuellaOriginal.getHeight());
							} else {
								ImagePlus iHuellaObservado = IJ.openImage(huella.getAbsolutePath());
								int width = lblHuellaObservado.getWidth();
								ImageProcessor imp = iHuellaObservado.getProcessor();
								imp = imp.resize(width);
								iHuellaObservado = new ImagePlus("observado", imp);
								lblHuellaObservado.setBounds(lblHuellaObservado.getBounds().x,
										lblHuellaObservado.getBounds().y, iHuellaObservado.getWidth(),
										iHuellaObservado.getHeight());
								lblHuellaObservado.setIcon(new ImageIcon(iHuellaObservado.getBufferedImage()));
								pnObservadoH.setBounds(pnObservadoH.getBounds().x, pnObservadoH.getBounds().y,
										iHuellaObservado.getWidth(), iHuellaObservado.getHeight());
							}
							pnObservadoH.setVisible(true);
							pnOriginalH.setVisible(true);
						}
					} else {
						lblHuellaOriginal.setText("Huella aceptada");
						lblHuellaOriginal.setVisible(true);
						pnObservadoH.setVisible(false);
						pnOriginalH.setVisible(false);
					}
					File pathFirma = new File(dniEncontrado.getAbsolutePath() + "\\firma");
					if (pathFirma.exists()) {
						File[] firmas = pathFirma.listFiles();
						for (File firma : firmas) {
							if (firma.getName().compareTo("original.jpg") == 0) {
								ImagePlus iFirmaOriginal = IJ.openImage(firma.getAbsolutePath());
								int width = lblFirmaOriginal.getWidth();
								ImageProcessor imp = iFirmaOriginal.getProcessor();
								imp = imp.resize(width);
								iFirmaOriginal = new ImagePlus("original", imp);
								lblFirmaOriginal.setBounds(lblFirmaOriginal.getBounds().x,
										lblFirmaOriginal.getBounds().y, iFirmaOriginal.getWidth(),
										iFirmaOriginal.getHeight());
								lblFirmaOriginal.setIcon(new ImageIcon(iFirmaOriginal.getBufferedImage()));
								/*
								 * pnOriginalF.setBounds(pnOriginalF.getBounds()
								 * .x, pnOriginalF.getBounds().y,
								 * iFirmaOriginal.getWidth(),
								 * iFirmaOriginal.getHeight());
								 */
							} else {
								ImagePlus iFirmaObservado = IJ.openImage(firma.getAbsolutePath());
								int width = lblFirmaObservado.getWidth();
								ImageProcessor imp = iFirmaObservado.getProcessor();
								imp = imp.resize(width);
								iFirmaObservado = new ImagePlus("observado", imp);
								lblFirmaObservado.setBounds(lblFirmaObservado.getBounds().x,
										lblFirmaObservado.getBounds().y, iFirmaObservado.getWidth(),
										iFirmaObservado.getHeight());
								lblFirmaObservado.setIcon(new ImageIcon(iFirmaObservado.getBufferedImage()));
								/*
								 * pnObservadoF.setBounds(pnObservadoF.getBounds
								 * ().x, pnObservadoF.getBounds().y,
								 * iFirmaObservado.getWidth(),
								 * iFirmaObservado.getHeight());
								 */
							}
							pnOriginalF.setVisible(true);
							pnObservadoF.setVisible(true);
						}
					} else {
						lblFirmaNoDisponible.setText("Firma aceptada");
						lblFirmaNoDisponible.setVisible(true);
						pnOriginalF.setVisible(false);
						pnObservadoF.setVisible(false);
					}
				} else {
					JOptionPane.showMessageDialog(null, "No se encontraron las imagenes");
				}
			}
		});
		tblAdherentes.setModel(tableModelAdherentes);

		scrollPane.setViewportView(tblAdherentes);
		JPanel pnAdherente = new JPanel();
		pnAdherente.setBorder(new TitledBorder(null, "ADHERENTE", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnAdherente.setBounds(448, 11, 396, 114);
		getContentPane().add(pnAdherente);
		pnAdherente.setLayout(null);

		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(10, 27, 153, 14);
		pnAdherente.add(lblNombre);

		txtAdherente = new JTextField();
		txtAdherente.setEditable(false);
		txtAdherente.setBounds(171, 21, 204, 20);
		pnAdherente.add(txtAdherente);
		txtAdherente.setColumns(10);

		JLabel lblPlanillon = new JLabel("Planillon:");
		lblPlanillon.setBounds(10, 52, 153, 14);
		pnAdherente.add(lblPlanillon);

		txtCodigoPlanillon = new JTextField();
		txtCodigoPlanillon.setEditable(false);
		txtCodigoPlanillon.setBounds(171, 46, 204, 20);
		pnAdherente.add(txtCodigoPlanillon);
		txtCodigoPlanillon.setColumns(10);

		txtDni = new JTextField();
		txtDni.setEditable(false);
		txtDni.setColumns(10);
		txtDni.setBounds(171, 71, 204, 20);
		pnAdherente.add(txtDni);

		JLabel lblDni = new JLabel("Dni:");
		lblDni.setBounds(10, 74, 153, 14);
		pnAdherente.add(lblDni);

		JTabbedPane tbpImagenes = new JTabbedPane(JTabbedPane.TOP);
		tbpImagenes.setBounds(448, 130, 396, 252);
		getContentPane().add(tbpImagenes);

		JPanel pnHuella = new JPanel();
		tbpImagenes.addTab("Huella", null, pnHuella, null);
		pnHuella.setLayout(null);

		lblHuellaNoDisponible = new JLabel("");
		lblHuellaNoDisponible.setBounds(195, 5, 0, 0);
		pnHuella.add(lblHuellaNoDisponible);

		pnOriginalH = new JPanel();
		pnOriginalH.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ORIGINAL",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnOriginalH.setBounds(4, 11, 162, 202);
		pnHuella.add(pnOriginalH);
		pnOriginalH.setLayout(null);

		lblHuellaOriginal = new JLabel("");
		lblHuellaOriginal.setBounds(6, 16, 150, 179);
		pnOriginalH.add(lblHuellaOriginal);

		pnObservadoH = new JPanel();
		pnObservadoH.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "OBSERVADO",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnObservadoH.setBounds(180, 11, 162, 202);
		pnHuella.add(pnObservadoH);
		pnObservadoH.setLayout(null);

		lblHuellaObservado = new JLabel("");
		lblHuellaObservado.setBounds(6, 16, 150, 174);
		pnObservadoH.add(lblHuellaObservado);

		JPanel pnFirma = new JPanel();
		tbpImagenes.addTab("Firma", null, pnFirma, null);
		pnFirma.setLayout(null);

		lblFirmaNoDisponible = new JLabel("");
		lblFirmaNoDisponible.setBounds(195, 5, 0, 0);
		pnFirma.add(lblFirmaNoDisponible);

		pnOriginalF = new JPanel();
		pnOriginalF.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ORIGINAL",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnOriginalF.setBounds(4, 11, 157, 209);
		pnFirma.add(pnOriginalF);
		pnOriginalF.setLayout(null);

		lblFirmaOriginal = new JLabel("");
		lblFirmaOriginal.setBounds(6, 16, 144, 193);
		pnOriginalF.add(lblFirmaOriginal);

		pnObservadoF = new JPanel();
		pnObservadoF.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "OBSERVADO",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnObservadoF.setBounds(172, 11, 144, 209);
		pnFirma.add(pnObservadoF);
		pnObservadoF.setLayout(null);

		lblFirmaObservado = new JLabel("");
		lblFirmaObservado.setBounds(6, 16, 144, 193);
		pnObservadoF.add(lblFirmaObservado);

		btnAceptado = new JButton("Aceptado");
		btnAceptado.setBounds(479, 393, 134, 23);
		getContentPane().add(btnAceptado);

		btnRechazado = new JButton("Rechazado");
		btnRechazado.setBounds(673, 393, 134, 23);
		getContentPane().add(btnRechazado);

		btnTerminar = new JButton("Terminar");
		btnTerminar.setBounds(38, 393, 89, 23);
		getContentPane().add(btnTerminar);

		// listener
		btnRechazado.addActionListener(this);
		btnAceptado.addActionListener(this);
		btnSalir.addActionListener(this);
		cmbPartido.addActionListener(this);
		btnTerminar.addActionListener(this);
	}

	class MyTableModel extends DefaultTableModel {
		String titles[] = { "DNI", "NOMBRE", "AP. PATERNO", "AP. MATERNO", "ESTADO" };

		@Override
		public int getColumnCount() {
			return titles.length;
		}

		public String getColumnName(int col) {
			return titles[col];
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

	}

	public void fillCmbPartido() {
		cmbPartido.removeAllItems();
		ArrayList<PartidoPolitico> listaObservados;
		try {
			listaObservados = siscomfiManager.queryAllPartidosConObservados();
			for (int i = 0; i < listaObservados.size(); i++) {
				PartidoPolitico p = (PartidoPolitico) listaObservados.get(i);
				cmbPartido.addItem(p.getIdPartidoPolitico() + " - " + p.getNombrePartido());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnSalir) {
			this.dispose();
		}

		if (e.getSource() == btnAceptado) {
			String estado = "ACEPTADO";
			tblAdherentes.setValueAt(estado, filaSeleccionada, 4);
			int aceptado = Integer.parseInt(txtAceptados.getText());
			aceptado++;
			txtAceptados.setText("" + aceptado);
		}

		if (e.getSource() == btnRechazado) {
			String estado = "ACEPTADO";
			if (estado.compareTo(tblAdherentes.getValueAt(filaSeleccionada, 4).toString()) == 0) {
				tblAdherentes.setValueAt("RECHAZADO", filaSeleccionada, 4);
				int aceptado = Integer.parseInt(txtAceptados.getText());
				aceptado--;
				txtAceptados.setText("" + aceptado);
			}
			tblAdherentes.setValueAt("RECHAZADO", filaSeleccionada, 4);
		}

		if (e.getSource() == cmbPartido) {
			// seleccionamos el partido que se esta viendo
			tableModelAdherentes.setRowCount(0);
			// clearTable();
			String partido = cmbPartido.getSelectedItem().toString();
			int idPartido = partido.charAt(0);
			pObservadosPartido = new File(pathObservados + "\\" + partido);
			if (pObservadosPartido.exists()){
				// buscar observados del partido
				File[] pAdhObservados = pObservadosPartido.listFiles();
				List<Adherente> adhObservados = new ArrayList<Adherente>();
				for (File pDni : pAdhObservados) {
					String dni = pDni.getName(); 
					Adherente adh = siscomfiManager.queryAdherenteByDni(dni);
					adhObservados.add(adh);
				}
				fillTable(adhObservados);
			}else{
				JOptionPane.showMessageDialog(this, "Partido no cuenta con adherentes observados");
			}
		}

		if (e.getSource() == btnTerminar) {
			for (int i = tableModelAdherentes.getRowCount() - 1; i >= 0; i--) {
				String dni = tableModelAdherentes.getValueAt(i, 0).toString();
				Adherente adh = siscomfiManager.queryAdherenteByDni(dni);
				String estado = tableModelAdherentes.getValueAt(i, 4).toString();
				siscomfiManager.updateEstadoAdherente(adh.getIdAdherente(), estado);
			}
			tableModelAdherentes.setRowCount(0);
		}
	}

	public void clearTable() {
		for (int i = tableModelAdherentes.getRowCount() - 1; i >= 0; i--)
			tableModelAdherentes.removeRow(i);
	}

	public void fillTable(List<Adherente> lista) {
		for (Adherente adh : lista) {
			tableModelAdherentes.addRow(
					new Object[] { "" + adh.getDni(), adh.getNombre(), adh.getAppPaterno(), adh.getAppMaterno(), "" });
		}
	}
}
