package sikla;

import java.io.File;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StageCreator extends JPanel implements KeyListener,MouseListener {

static final int USIZE=32;
static final byte SPACE=0, GOAL=1, BLOCK=2, GBLOCK=3, WALL=4, DELETE=-1, RESIZE=-2;

byte[][] map;
byte celement=-3, spos[]= new byte[] {-1, -1}; //spos is the position of Sikla
int bnd[] = new int[2];
SiklaMain par;



StageCreator(SiklaMain pr) {
	super();
	this.par = pr;
	setBackground(new Color(130,130,130));
	addKeyListener(this);
	addMouseListener(this);
}//Constructor



public void paintComponent(Graphics g) {
	super.paintComponent(g);
	
	if (map==null) {celement=-2; return;}
	int i=0, j=0;
	//draw the map
	for (;i<bnd[0];++i) {
	for (;j<bnd[1];++j) {
	
	switch (map[i][j]) {
	case WALL:
	g.drawImage(GamePanel.im2,USIZE*(i+1),USIZE*(j+1),this);
	break;
	case BLOCK: case GBLOCK:
	g.drawImage(GamePanel.im3,USIZE*(i+1),USIZE*(j+1),this);
	break;
	case GOAL:
	g.drawImage(GamePanel.im4,USIZE*(i+1),USIZE*(j+1),this);
	break;
	case 0:
	break;
	}//switch
	
	}//for j
	j=0;
	}//for i
	
	//draw sikla if exists
	if (spos[0]>=0 && spos[1]>=0) g.drawImage(GamePanel.im1,USIZE*(spos[0]+1),USIZE*(spos[1]+1),this);
	
	//draw the borders.
	j = bnd[0]+1;
	for (i=0;i<bnd[1]+2;++i) {
	g.drawImage(GamePanel.im2,0,USIZE*i,this);
	g.drawImage(GamePanel.im2,USIZE*j,USIZE*i,this);
	}//for
	j = bnd[1]+1;
	for (i=1;i<bnd[0]+1;++i) {
	g.drawImage(GamePanel.im2,USIZE*i,0,this);
	g.drawImage(GamePanel.im2,USIZE*i,USIZE*j,this);
	}//for
}//PAINT_COMPONENT



void putItem(byte i, byte j) {
	if (map==null) celement = RESIZE;
	
	if(celement==RESIZE) {resizeStage(i,j); repaint(); return;}
	
	if (i<0 || i>=map.length) return;
	if (j<0 || j>=map[0].length) return;
	
	if (celement==0 && (map[i][j]==SPACE || map[i][j]==GOAL)) { // put sikla
	spos[0] = i;
	spos[1] = j;
	}//if 0
	
	else if (celement==GOAL) {
	map[i][j]=GOAL;
	}//else if GOAL
	
	else if (celement==BLOCK) {
	if (map[i][j]==GOAL || map[i][j]==GBLOCK) {map[i][j]=GBLOCK;}
	else {map[i][j]=BLOCK;}
	
	if(i==spos[0] && j==spos[1]) {spos[0]=-1; spos[1]=-1;}//remove sikla
	}//else if BLOCK
	
	else if (celement==WALL) {
	map[i][j] = WALL;
	
	if(i==spos[0] && j==spos[1]) {spos[0]=-1; spos[1]=-1;}
	}//else if BLOC
	
	else if (celement==DELETE) {
	if(i==spos[0] && j==spos[1]) {spos[0]=-1; spos[1]=-1;}//remove sikla only
	else if (map[i][j]==GBLOCK) {map[i][j] = GOAL;}
	else {map[i][j]=0;}
	}//else if DELETE
	
	repaint();
	
}//PUT_ITEM


private void resizeStage(byte i, byte j) {
//the general idea is that we should create a new byte[][] array only if it is needed
	bnd[0]=i; bnd[1]=j;
	if (map==null) {map = new byte[i][j]; return;}
	
	if (bnd[0]>map.length && bnd[1]>map[0].length) {
	byte[][] tmap = new byte[bnd[0]][bnd[1]];
	int l1 = map.length, l2 = map[0].length; //the smaller ones
	for (int k=0; k<l1; k++) System.arraycopy(map[k],0, tmap[k],0, l2);
	map = tmap;
	}//if both are greater
	
	else if (bnd[0]>map.length) {
	int k;
	byte[][] tmap = new byte[bnd[0]][];
	for (k=0; k<map.length; k++) tmap[k] = map[k];
	for (; k<bnd[0]; ++k) tmap[k] = new byte[map[0].length];
	map = tmap;
	}//only length is greater
	
	else if (bnd[1]>map[0].length) {
	byte[][] tmap = new byte[map.length][bnd[1]];
	for (int k=0; k<map.length; ++k) System.arraycopy(map[k],0, tmap[k],0, map[0].length);
	map = tmap;
	}//else  height if greater
}//RESIZE_STAGE


void editStage() {
	
	int ch, hlp=-1;
	boolean prs;
	String str = "Διαλέξτε επίπεδο.";
	JTextField txt = new JTextField(5);
	DataInputStream din = null;
	Scanner sc;
	JComboBox cmb = new JComboBox<String>(new String[] {"Προσωπικά επίπεδα","Κύρια επίπεδα"});
	
	Object[] ob, obj = new Object[] {cmb, txt, "Άνοιγμα" };
	
	if (par.pers==0) par.pers = FileManager.numOfStages(par.pf);
	for (;;) {
	ch = JOptionPane.showOptionDialog(this, str, "Διαλέξτε", JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
		null, obj, txt);
	
	
	if (ch==JOptionPane.CLOSED_OPTION) return; //1. If the user pressed close button, return
	
	sc = new Scanner(txt.getText()); //2. Get the text. If it has an integer go on...
	if (!sc.hasNextInt()) {str = "Παρακαλώ δώστε έναν θετικό ακέραιο."; continue;}
	
	ch = sc.nextInt(); //3. If the number is not positive stop
	if (ch<0) {str = "Παρακαλώ δώστε έναν θετικό ακέραιο."; continue;}
	
	
	prs = cmb.getSelectedIndex() == 0; //4. see if it is allowed to read this stage
	if (prs && ch>par.pers) {str = "Υπάρχουν μόνο "+par.pers+" επίπεδα. Διαλάξετε ένα από αυτά"; continue;}
	else if (!prs && ch>par.avstages) {
	str = "Δεν μπορείτε να ανοίξετε ένα επίπεδο όταν δεν έχετε νικήσει τα προηγούμε νά του!";
	continue;
	}//if
	
	try {
	din = new DataInputStream (new FileInputStream ((prs)? par.pf : SiklaMain.fmain));
	}//try
	catch (java.io.FileNotFoundException ex) {}
	
	//5. See if the stage exists
	hlp =  FileManager.skipStages(din, null, ch-1);
	if (hlp!=0) {str = "Σφάλμα κατά την ανάγνωση των επιπέδων."; continue;}
	
	break; //finally!!!
	}//for (;;)
	
	FileManager.loadStage(par, ch, prs, false);
}//editStage


int setStage(int num, boolean prs, Object[] data) {
	map = (byte[][]) data[0];
	spos = (byte[]) data[1];
	bnd[0] = map.length; bnd[1] = map[0].length;
	par.crstage = num;
	par.crpmode = prs;
	par.crloadf = (prs)? par.pf : SiklaMain.fmain;
	repaint();
	return 0;
}//SET_STAGE


byte[][] getActiveMap() {
	if (map==null) return null;
	byte[][] ret = new byte[bnd[0]][bnd[1]];
	
	for (int k=0; k<ret.length; ++k) System.arraycopy(map[k],0, ret[k],0, bnd[1]);
	return ret;
}//GET_ACTIVE_MAP



public void keyPressed(KeyEvent e) {}
public void keyReleased(KeyEvent e) {}
public void keyTyped(KeyEvent e) {}

public void mouseClicked(MouseEvent me) {}
public void mouseEntered(MouseEvent me) {}
public void mouseExited(MouseEvent me) {}
public void mousePressed(MouseEvent me) {}

public void mouseReleased(MouseEvent me) {
	if (me.getButton()==MouseEvent.BUTTON1)
	putItem( (byte)(me.getX()/USIZE-1), (byte)(me.getY()/USIZE-1) );
}//MOUSE_RELEASED

}//class
