package sikla;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;


public class SiklaMain extends JFrame implements ActionListener,MouseListener {

static final int WAIT=0, PLAY=1, CREATE=2, PROFILE=3;

static final File fmain = new File("sikla/stages");
static File pf, loadf, crloadf;
static int stages = FileManager.numOfStages(fmain); //WHEN GAME IS COMPLETE, HERE WE WILL JUST PUT THE NUMBER OF STAGES
static GridBagConstraints c = new GridBagConstraints();
static String[] profiles;

Container[] cnt = new Container[3];
int mode=0;

//-----profile variables-----
int avstages=1, bfr=0, pers=0, cstage=1;
boolean pmode;
String profile="";

//-----------MENU------------
JMenuBar mbar = new JMenuBar();
JMenu m1 = new JMenu("Παιχνίδι"), m2 = new JMenu("Πληροφορίες");
JMenuItem[] mi = new JMenuItem[] {new JMenuItem("Νέο"), new JMenuItem("Προφίλ"), new JMenuItem("Αποθήκευση"),
new JMenuItem("Επιλογή επιπέδου"), new JMenuItem("Προσωπικό επίπεδο"),
new JMenuItem("Δημιουργία επιπέδου"), new JMenuItem("Έξοδος")};

JMenuItem mia = new JMenuItem("Οδηγίες"), mib = new JMenuItem("Περί Sikla...");

//PROFILE MODE ELEMENTS
JLabel prm;
JScrollPane scr;
JList<String> prl;
JPanel prp;
JButton[] prb = new JButton[] {new JButton("Άνοιγμα"),new JButton("Νέο"),new JButton("Διαγραφή"),new JButton("Πίσω")};

//GAME MODE ELEMENTS
GamePanel gp = new GamePanel(this);
StageCreator cr;
JPanel gmp = new JPanel(new GridBagLayout());
JLabel lb = new JLabel(" ");
JButton[] gmb = new JButton[] {new JButton(new ImageIcon("./sikla/data/previous.gif")),
new JButton(new ImageIcon("./sikla/data/restart.png")),
new JButton(new ImageIcon("./sikla/data/next.gif"))};

//CREATE MODE ELEMENTS
int crstage=0;
boolean crpmode;
JPanel crp, crp2;
JButton[] crb;
JMenuBar crmb;
JMenu crm;
JMenuItem[] crmi;

SiklaMain() {
	
	int i;
	setTitle("Sikla Puzzle Game");
	setVisible(true);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setSize(700,700);
	setLayout(new FlowLayout());
	
	constructMenu();
	
	profiles = new File("./sikla/profiles/").list();
	setMode(PROFILE);
	
	if (mode==PLAY) {gp.requestFocusInWindow();}
	else if (mode==CREATE) {cr.requestFocusInWindow();}
	
	
	validate();
	
	//cosntruct gmp
	final Dimension d = new Dimension(22,22);
	for (i=0; i<gmb.length; ++i) {
	gmb[i].setPreferredSize(d);
	gmp.add(gmb[i]);
	gmb[i].addActionListener(this);
	}//for
	
	for (i=0; i<mi.length;++i) mi[i].addActionListener(this);
	for (i=0; i<prb.length;++i) prb[i].addActionListener(this);
	mia.addActionListener(this);
	mib.addActionListener(this);
	addMouseListener(this);
}//constructor


private void constructMenu() {
	int i=0;
	for (; i<3;++i) m1.add(mi[i]);
	m1.add(new JSeparator());
	
	for (; i<6;++i) m1.add(mi[i]);
	m1.add(new JSeparator());
	m1.add(mi[6]);
	
	m2.add(mia);
	m2.add(mib);
	
	mbar.add(m1);
	mbar.add(m2);
	setJMenuBar(mbar);
	for (i=1; i<6; i++) mi[i].setEnabled(false);
}//CONSTRUCT_MENU


void setMode(int md) {
	if (md==PLAY) {
	if (mode==PLAY) return; //its already done!
	if (mode!=0 && cnt[mode-1]!=null) remove(cnt[mode-1]);
	mode = md;
	
	if (cr!=null) {cr.map=null; crstage=0; cr.spos[0]=-1; cr.spos[1]=-1;}
	
	if (cnt[md-1]==null) { //costruct it!
	cnt[md-1] = new Container();
	cnt[md-1].setPreferredSize(new Dimension(690,680));
	cnt[md-1].setLayout(new GridBagLayout());
	
	c.insets.set(4,15,0,0);
	c.gridx=GridBagConstraints.RELATIVE;
	c.gridy=0;
	c.gridwidth=1;
	c.weighty=0;
	c.weightx=0;
	c.fill=GridBagConstraints.NONE;
	cnt[md-1].add(lb,c);
	
	c.anchor=GridBagConstraints.EAST;
	cnt[md-1].add(gmp,c);
	
	
	c.anchor=GridBagConstraints.CENTER;
	c.fill=GridBagConstraints.BOTH;
	c.gridx=0;
	c.gridy=1;
	c.gridwidth=2;
	c.weighty=1;
	c.weightx=1;
	cnt[md-1].add(gp,c);
	}//if
	
	
	gp.requestFocusInWindow();
	
	c.anchor=GridBagConstraints.CENTER;
	c.fill=GridBagConstraints.BOTH;
	add(cnt[md-1],c);
	
	//menu...
	for (int i=1; i<6; i++) mi[i].setEnabled(true);
	}//if play
	
	

	else if(md==CREATE) {
	int i;
	if (mode==CREATE) return; //its already done!
	if (cr==null) {cr = new StageCreator(this); crstage=0;}
	
	if (mode!=0 && cnt[mode-1]!=null) remove(cnt[mode-1]);
	mode = md;
	
	if (cnt[md-1]==null) { //construct it!
	cnt[md-1] = new Container();
	cnt[md-1].setPreferredSize(new Dimension(690,680));
	cnt[md-1].setLayout(new GridBagLayout());
	
	crp = new JPanel(new FlowLayout());
	crp2 = new JPanel(new FlowLayout());
	crmb = new JMenuBar();
	crm = new JMenu("Επιλογές");
	crb = new JButton[] {new JButton(new ImageIcon("./sikla/data/sikla.png")),
	new JButton(new ImageIcon("./sikla/data/wall.png")),
	new JButton(new ImageIcon("./sikla/data/block.png")),
	new JButton(new ImageIcon("./sikla/data/goal.png")),
	new JButton("X"), new JButton("S"),
	new JButton(new ImageIcon("./sikla/data/previous.gif")),
	new JButton(new ImageIcon("./sikla/data/restart.png")),
	new JButton(new ImageIcon("./sikla/data/next.gif"))};
	
	crmi = new JMenuItem[] {new JMenuItem("Άνοιγμα"), new JMenuItem("Αποθήκευση"),
	new JMenuItem("Διαγραφή"), new JMenuItem("Τέλος")};
	
	Dimension ds = new Dimension(45,35);
	for (i=0; i<6;++i) {
	crb[i].setPreferredSize(ds);
	crb[i].addActionListener(this);
	}//for
	ds = new Dimension(22,22);
	for (; i<crb.length; ++i) {
	crb[i].setPreferredSize(ds);
	crb[i].addActionListener(this);
	}//for
	
	for (i=0; i<crmi.length;++i) {crm.add(crmi[i]); crmi[i].addActionListener(this);}
	crmb.add(crm);
	
	for (i=0; i<6;++i) crp.add(crb[i],c);
	crp.add(crmb);
	for (; i<crb.length;++i) crp2.add(crb[i],c);
	
	c.gridx=GridBagConstraints.RELATIVE; //add buttons and menu
	c.insets.set(4,15,0,0);
	c.gridy=0;
	c.gridwidth=1;
	c.weighty=0;
	c.weightx=0;
	c.fill=GridBagConstraints.NONE;
	cnt[md-1].add(crp,c);
	
	c.anchor=GridBagConstraints.EAST;
	cnt[md-1].add(crp2,c);
	
	c.gridx=0;
	c.gridy=1;
	c.gridwidth=2;
	c.weighty=1;
	c.weightx=1;
	c.fill=GridBagConstraints.BOTH;
	cnt[md-1].add(cr,c);
	}//if
	
	
	c.anchor=GridBagConstraints.CENTER;
	c.fill=GridBagConstraints.BOTH;
	add(cnt[md-1],c);
	
	for (i=2; i<6; i++) mi[i].setEnabled(false);
	}//else if create
	
	
	
	else if (md==PROFILE) {
	
	if (mode==PROFILE) return; //its already done!
	if (mode!=0 && cnt[mode-1]!=null) remove(cnt[mode-1]);
	bfr = mode;
	mode = md;
	
	if (cr!=null)
	{ cr.map = null; crstage=0; cr.spos[0]=-1; cr.spos[1]=-1; }//in case we were in create Mode
	
	if (cnt[md-1]==null) {
	cnt[md-1] = new Container();
	cnt[md-1].setPreferredSize(new Dimension(690,680));
	cnt[md-1].setLayout(new GridBagLayout());
	
	prm = new JLabel("Επιλέξτε προφίλ:");
	prl = new JList<String> (profiles);
	prl.addMouseListener(this);
	scr = new JScrollPane(prl);
	scr.setPreferredSize(new Dimension(180,80));
	prp = new JPanel(new FlowLayout());
	for(int i=0;i<prb.length;++i) prp.add(prb[i]); //add buttons to their container
	
	c.insets.set(50,3,0,0); //put label and list
	c.anchor=GridBagConstraints.NORTH;
	c.gridy=0; c.gridx=GridBagConstraints.RELATIVE;
	c.gridwidth=2;
	c.weighty=0;
	c.weightx=0;
	c.fill=GridBagConstraints.NONE;
	cnt[md-1].add(prm,c);
	cnt[md-1].add(scr,c);
	
	c.gridx=0; c.gridy=2;
	c.gridwidth=4;
	cnt[md-1].add(prp,c);
	}//if
	
	
	c.fill=GridBagConstraints.BOTH;
	c.anchor=GridBagConstraints.CENTER;
	if (prl.getModel().getSize()>0) prl.setSelectedIndex(0);
	add(cnt[md-1],c);
	
	if (prl.getModel().getSize()==0) {
	validate();
	repaint();
	if(createProfile()) FileManager.loadStage(this,1,false, true);
	return;
	}//if no profiles exist
	
	//menu...
	mi[1].setEnabled(false);
	mi[2].setEnabled(false);
	
	boolean bln = !profile.equals("");
	mi[3].setEnabled(bln);
	mi[4].setEnabled(bln);
	mi[5].setEnabled(bln);
	}//else if profile
	
	
	validate();
	
	repaint();
	if (mode==PLAY) {gp.requestFocusInWindow();}
	else if (mode==CREATE) {cr.requestFocusInWindow();}
}//SET_MODE


private boolean createProfile() {
	File parnt;
	String str = JOptionPane.showInputDialog(null,"Γράψτε το όνομά σας...","Δημιουργία Προφίλ",JOptionPane.PLAIN_MESSAGE);
	try {
	for (;;) {
	if (str==null) {return false;}
	else if (str.equals("")) {str = JOptionPane.showInputDialog(null,"Παρακαλώ δώστε ένα όνομα!","Κενό όνομα",JOptionPane.PLAIN_MESSAGE);}
	else {
	parnt = new File("./sikla/profiles/"+str);
	if (parnt.exists()) {
	str = JOptionPane.showInputDialog(null,"Το όνομα που δώσατε υπάρχει ήδη.\nΠαρακαλώ δώστε άλλο όνομα.",
	"Όνομα υπάρχει",JOptionPane.PLAIN_MESSAGE);
	}//if
	else {break;}
	}//else
	}//for ;;
	
	if (!parnt.mkdir()) {
	JOptionPane.showMessageDialog(null,"Πρόβλημα κατά τη δημιουργία του προφίλ σας...","Σφάλμα",JOptionPane.ERROR_MESSAGE); 
	return false;
	}//if
	
	if (!(new File(parnt,"data")).createNewFile()) {
	JOptionPane.showMessageDialog(null,"Πρόβλημα κατά τη δημιουργία του προφίλ σας...","Σφάλμα",JOptionPane.ERROR_MESSAGE); 
	return false;
	}//if
	
	if (!(new File(parnt,"stages")).createNewFile()) {
	JOptionPane.showMessageDialog(null,"Πρόβλημα κατά τη δημιουργία του προφίλ σας...","Σφάλμα",JOptionPane.ERROR_MESSAGE); 
	return false;
	}//if
	
	setDefaultValues(str, true);
	profiles = (new File("./sikla/profiles/")).list();
	if (prl!=null) prl.setListData(profiles);
	return true;
	}//try
	catch (java.io.IOException ex) {return false;}
}//CREATE_PROFILE



void deleteProfile() {
	if (prl == null || prl.getModel().getSize()<=0) return;
	
	String str = prl.getSelectedValue().toString();
	
	if (str.equals(profile)) {
	setDefaultValues(null, true);
	bfr=0;
	for (int i=1; i<6; i++) mi[i].setEnabled(false);
	}//if
	
	File prnt = new File("./sikla/profiles/"+str);
	for (File ins : prnt.listFiles()) ins.delete();
	prnt.delete();
	
	profiles = prnt.getParentFile().list();
	prl.setListData(profiles);
	if (prl.getModel().getSize()>0) prl.setSelectedIndex(0);
}//DELETE_PROFILE


void setDefaultValues(String prof, boolean all) {
	//just game variables like in the begining. This is used when we load or create a profile...
	//if all is true, we initialize also the variables that are normally saved. This is when data file is empty
	
	if (prof==null) {prof=""; setTitle("Sikla Puzzle Game"); pers=0;}
	else {
	setTitle("Sikla Puzzle Game: "+prof);
	SiklaMain.pf = new File("sikla/profiles/"+prof+"/stages");
	pers = FileManager.numOfStages(pf);
	}//else
	
	profile=prof;
	
	if (cr!=null)
	{ cr.map=null; cr.spos[0]=-1; cr.spos[1]=-1; crpmode=false; crstage=0; }
	
	
	if (all) {
	gp.map=null; gp.posx=0; gp.posy=0; gp.gnum=0; gp.status=0;
	pmode = false;
	loadf = fmain;
	avstages = 1;
	cstage = 1;
	}//if all
	
	if(lb!=null)
	if (loadf==SiklaMain.fmain) {lb.setText("Επίπεδο "+cstage);}
	else {lb.setText("Προσωπικό επίπεδο "+cstage);}
}//SET_DEFAULT_VALUES


public void actionPerformed(ActionEvent e) {
	Object s = e.getSource();
	
	//--------------MENU BAR-------------------
	if (s==mi[0]) { //new game
	if (createProfile()) FileManager.loadStage(this,1,false, true);
	}//if mi[0]
	
	else if (s==mi[1]) { //open
	setMode(PROFILE);
	}//if mi[1]
	
	else if (s==mi[2]) { //Save
	int lv = (pmode)? -cstage : cstage;
	FileManager.saveGame(this,true);
	}//if mi[2]
	
	else if (s==mi[3]) { //choose level
	if (!loadf.equals(fmain)) avstages = FileManager.numOfStages(fmain);
	
	String str = JOptionPane.showInputDialog("Ποιο επίπεδο θέλετε;");
	int i=1, j=0;
	for(;;) {
	if (str==null) return;
	try{
	j = Integer.parseInt(str);
	if (j>avstages) {
	JOptionPane.showMessageDialog(null,"Πρέπει πρώτα να νικήσετε τα προηγούμενα επίπεδα.","Sikla",JOptionPane.PLAIN_MESSAGE);
	return;
	}//if
	pmode=false;
	i = FileManager.loadStage(this,j,false, true);
	}//try
	catch (NumberFormatException exp) {i=-2;} //sign that the user gave non-integer
	
	if(i==-2) {str = JOptionPane.showInputDialog("Παρακαλώ δώστε ακέραιο αριθμό. Ποιο επίπεδο θέλετε;");}
	else if (i>0) {str = JOptionPane.showInputDialog("Δυστυχώς υπάρχουν μόνο "+i+" επίπεδα. Ποιο επίπεδο θέλετε;");}
	else {return;}
	}//for ;;
	}//else if mi[3]
	
	
	else if (s==mi[4]) { //choose personal level
	
	if (profile.equals("")) {
	JOptionPane.showMessageDialog(null,"Πρέπει πρώτα να επιλέξετε προφίλ.","Sikla",JOptionPane.WARNING_MESSAGE);
	return;
	}//if
	
	if (pf!=null && !loadf.equals(pf)) pers = FileManager.numOfStages(pf);
	
	String str = JOptionPane.showInputDialog("Ποιο επίπεδο θέλετε;");
	int i=1, j=0;
	for(;;) {
	if (str==null) return;
	try{
	j = Integer.parseInt(str);
	if (j>pers) {
	str = JOptionPane.showInputDialog("Δυστυχώς υπάρχουν μόνο "+pers+" προσωπικά επίπεδα. Ποιο επίπεδο θέλετε;");
	return;
	}//if
	pmode=true;
	i = FileManager.loadStage(this,j,true, true);
	}//try
	catch (NumberFormatException exp) {i=-2;} //sign that the user gave non-integer
	
	if(i==-2) {str = JOptionPane.showInputDialog("Παρακαλώ δώστε ακέραιο αριθμό. Ποιο επίπεδο θέλετε;");}
	else if (i>0) {str = JOptionPane.showInputDialog("Δυστυχώς υπάρχουν μόνο "+i+" επίπεδα. Ποιο επίπεδο θέλετε;");}
	else {return;}
	}//for ;;
	}//else if mi[4]
	
	
	else if (s==mi[5]) { //create
	setMode(CREATE);
	}//else if mi[5]
	
	
	else if (s==mi[6]) { //exit
	System.exit(0);
	}//else if mi[6]
	
	
	//-----------------GAME BUTTONS-------------------
	else if (s==gmb[0] ) { //previous
	if (cstage!=1) FileManager.loadStage(this,cstage-1,pmode,true);
	gp.requestFocusInWindow();
	}//else if gmb[0]
	
	else if (s==gmb[1]) { //restart
	FileManager.loadStage(this,cstage,pmode,true);
	gp.requestFocusInWindow();
	}//else if gmb[1]
	
	else if (s==gmb[2]) { //next
	boolean ret=false;
	if (!pmode && (cstage==avstages || cstage==stages) ) ret=true;
	else if (pmode && cstage==pers) ret=true;
	if (!ret) FileManager.loadStage(this,cstage+1,pmode,true);
	gp.requestFocusInWindow();
	}//else if gmb[2]


	//--------------CREATE ELEMENTS-------------------
	if (crb!=null) {
	if (s==crb[0]) { //Sikla
	if (cr==null) return;
	cr.celement = 0;
	}//else if crb[0]
	
	else if (s==crb[1]) { //Wall
	if (cr==null) return;
	cr.celement = StageCreator.WALL;
	}//else if crb[1]
	
	else if (s==crb[2]) { //Block
	if (cr==null) return;
	cr.celement = StageCreator.BLOCK;
	}//else if crb[2]
	
	else if (s==crb[3]) { //Goal
	if (cr==null) return;
	cr.celement = StageCreator.GOAL;
	}//else if crb[3]
	
	else if (s==crb[4]) { //Delete
	if (cr.map==null) return;
	cr.celement = StageCreator.DELETE;
	}//else if crb[4]
	
	else if (s==crb[5]) { //Resize
	if (cr.map==null) return;
	cr.celement = StageCreator.RESIZE;
	}//else if crb[5]
	
	else if (s==crb[6]) { //one level back
	if (crstage<=1) return;
	FileManager.loadStage(this, crstage-1, crpmode, false);
	}//else if crb[6]
	
	else if (s==crb[7]) { //restart level
	if (crstage==0) return;
	FileManager.loadStage(this, crstage, crpmode, false);
	}//else if crb[7]
	
	else if (s==crb[8]) { //restart level
	if (crstage==0) return;
	if (crpmode && crstage==pers) return;
	if (!crpmode && crstage==avstages) return;
	FileManager.loadStage(this, crstage+1, crpmode, false);
	}//else if crb[8]
	
	
	else if (s==crmi[0]) { //open stage to edit
	cr.editStage();
	}//crmi[0]
	
	else if (s==crmi[1]) { //Save Stage
	FileManager.saveStage(cr);
	}//else if crmi[1]
	
	else if (s==crmi[2]) { //Delete stage
	if (crpmode) { FileManager.deleteStage(this, pf, crstage); crstage=0; crpmode=true;}
	}//cmi[2]
	
	else if (s==crmi[3]) { //Exit
	setMode(PLAY);
	}//else if cmi[3]
	}//if crb!=null
	
	
	//--------------PROFILE ELEMENTS-------------------
	if (prb!=null) {
	
	if (s==prb[0]){ //open profile
	setMode(PLAY);
	if (prl.getModel().getSize()<=0) { //check if the list was empty
	createProfile();
	return;
	}//if
	FileManager.loadGame(this, prl.getSelectedValue().toString()); //load the correct profile
	}//if prb[0]
	
	else if (s==prb[1]){ //new
	if (createProfile()) FileManager.loadStage(this,1,false,true);
	}//else if prb[1]
	
	else if (s==prb[2]){ //delete
	deleteProfile();
	}//else if prb[2]
	
	else if (s==prb[3]){ //back
	if (bfr!=0) setMode(bfr);
	}//else if prb[3]
	
	}//if prb!=null
	
}//ACTION_PERFORMED

		//--------------MOUSE EVENTS-----------------
public void mouseClicked(MouseEvent me) {
	if (prl.getModel().getSize()<=0) return;
	
	if(prl!=null && me.getComponent()==prl && me.getClickCount()==2) {
	FileManager.loadGame(this, prl.getSelectedValue().toString());
	}//if
}//MOUSE_CLICKED

public void mouseReleased(MouseEvent me) {
	if (mode==PLAY) {gp.requestFocusInWindow();}
	else if (mode==CREATE) {cr.requestFocusInWindow();}
}//MOUSE_RELEASED

public void mouseEntered(MouseEvent me) {}
public void mouseExited(MouseEvent me) {}
public void mousePressed(MouseEvent me) {}


public static void main(String args[]) {
	try {
	new SiklaMain();
	
	}//try
	catch(Exception ex) {System.out.print(ex.toString()+"\n");}
}//main
}//class
