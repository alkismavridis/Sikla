package sikla;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.EOFException;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class FileManager {

private static final byte SPACE=0, GOAL=1, BLOCK=2, GBLOCK=3, WALL=4;


static int numOfStages(File fl) {
	
	int ret=0;
	DataInputStream sc=null;
	
	try {
	sc = new DataInputStream(new FileInputStream(fl));
	for (;;) if (sc.readByte()==-1) ret++;
	}//try
	catch (EOFException ex) {return ret;}
	catch (java.io.IOException ex) {
	JOptionPane.showMessageDialog(null, "Το αρχείο που επιλέξατε δε βρέθηκε.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
	return -1;
	}//catch
}//NUM_OF_STAGES


static int skipStages(DataInputStream sc, DataOutputStream wr,int st) {
	//if you put negative argument it will read until the end...
	if (st==0) return 0;
	int c=0;
	
	if (wr==null) {
	try {
	for (;;)
	if (sc.readByte()==-1) {
	c++;
	if (c==st) return 0;
	}//if
	}//try
	catch (EOFException ex) {return (c==0 || st==-1)? -1:c;}
	catch (java.io.IOException ex) {return -2;}
	}// if null
	
	else {
	try {
	byte b = sc.readByte();
	for (;;) {
	wr.writeByte(b);
	if (b==-1) { c++; if (c==st) return 0;}
	b = sc.readByte();
	}//for ;;
	}//try
	catch (EOFException ex) {return (c==0 || st==-1)? -1:c;}
	catch (java.io.IOException ex) {return -2;}
	}//else
}//SKIP_STAGES


static int saveGame(SiklaMain sm, boolean wrmap) {
	//data must contains:
	// { profile_name, level_to_write, byte[][] map, int[2] pos, int[2] avalable_stages_and_moves }
	try {
	
	DataOutputStream  wr = new DataOutputStream(
	new FileOutputStream(new File("./sikla/profiles/"+sm.profile+"/data"),false));
	
	byte[] pos = new byte[] {sm.gp.posx, sm.gp.posy};
	
	wr.writeInt(sm.avstages); // write the basic info
	wr.writeInt(sm.cstage);
	wr.writeInt(sm.gp.moves);	
	if (wrmap) writeMap(wr, sm.gp.map, pos, true);	//write the map, without the borders!
	
	wr.close();
	JOptionPane.showMessageDialog(null,"Παιχνίδι αποθηκεύθηκε!","Αποθήκευση",JOptionPane.PLAIN_MESSAGE);
	return 0;
	}//try
	catch (Exception ex) {ex.printStackTrace(); return 1;}
}//SAVE_GAME



static Object[] readMap(DataInputStream sc, boolean putbor) {
	//the DataInputStream must be ready to read the size of the stage and so on...
	//returns an array contains:
	// { byte[][] map, byte[] pos, Integer gnum }

	byte i1=0, i2=0, i3=0, i4=0, v=0, j=0, blks=0, gls=0, pos[] = new byte[2], nxt;
	int i=0, bor = (putbor)? 2:0;
	byte map[][], gnum;
	
	try {
	map = new byte[sc.readByte()+bor][sc.readByte()+bor]; //1.read size of the stage

	bor/=2;
	pos[0] = (byte)(sc.readByte()+bor);	//2.read position of sikla
	pos[1] = (byte)(sc.readByte()+bor);
	nxt = sc.readByte();

	//3. -----load the goals, blocks, gblocks and walls-----
	for (v=1;v<5;++v) {
	while (nxt!=-3 && nxt!=-1 ) {
	
	//1. reading...
	i2=-1; i4=-1;
	
	if (nxt==-2) { //means you have to read two values. From...to
	i1=sc.readByte();
	i2=sc.readByte();
	}//if >
	else i1 = nxt; //the one you read.
	
	nxt = sc.readByte(); //the same story...
	if (nxt==-2) {
	i3=sc.readByte();
	i4=sc.readByte();
	}//if >
	else i3 = nxt;
	
	//2. writing
	if (i2==-1 && i4==-1) { //write one element
	map[i1+bor][i3+bor]=v;
	if (v==BLOCK) blks++;
	else if (v==GOAL) gls++;
	}//if
	
	
	else if(i4==-1) { //write from...to horizontaly
	for (i=i1;i<=i2;++i) map[i+bor][i3+bor]=v;
	if (v==BLOCK) blks+=Math.abs(i2-i1)+1;
	else if (v==GOAL) gls+=Math.abs(i2-i1)+1;
	}//else if i4=-1
	
	
	else if(i2==-1) { //write from...to vertically
	for (i=i3;i<=i4;++i) map[i1+bor][i+bor]=v;
	if (v==BLOCK) blks+=Math.abs(i4-i3)+1;
	else if (v==GOAL) gls+=Math.abs(i4-i3)+1;
	}//else if i2=-1
	
	
	else { //write square
	for (i=i1;i<=i2;++i)
	for (j=i3;j<=i4;++j) map[i+bor][j+bor]=v;

	if (v==BLOCK) blks+=(Math.abs(i4-i3)+1)*(Math.abs(i2-i1)+1);
	if (v==GOAL) gls+=(Math.abs(i4-i3)+1)*(Math.abs(i2-i1)+1);
	}//else
	
	nxt = sc.readByte();
	}//while
	nxt = sc.readByte();
	}//for v
	
	
	gnum = (byte)(Math.min(blks,gls));
	
	//put borders
	if (putbor) {
	for (i=0;i<map.length;++i) {map[i][0]=WALL; map[i][map[0].length-1]=WALL;}
	for (i=0;i<map[0].length;++i) {map[0][i]=WALL; map[map.length-1][i]=WALL;}
	}//if
	
	return new Object[] {map, pos, gnum};
	}//try
	
	catch (EOFException ex) {return null;}
	catch (Exception ex) {System.out.print(ex.toString()+"\n"); return null;}
}//READ_MAP



static int writeMap(DataOutputStream wr, byte[][] map, byte[] pos, boolean delborders) {
	
	//1. Return if everything is not fine
	if (pos[0]==-1 || pos[1]==-1) {
	JOptionPane.showMessageDialog(null,"Παρακαλώ τοποθετίστε κάπου τον Sikla!","Προσοχή",JOptionPane.INFORMATION_MESSAGE);
	return 1;
	}//if
	
	byte i=0, j=0, i2, j2, i3, j3, k, v=1;
	boolean down, right, corner;
	
	//1. Clone the map!
	byte[][] tmap;
	
	if (!delborders) {
	tmap = new byte[map.length][map[0].length];
	for(i=0; i<map.length; ++i) System.arraycopy(map[i],0,tmap[i],0,tmap[0].length);
	}//if
	
	else {
	tmap = new byte[map.length-2][map[0].length-2];
	for(i=0; i<tmap.length; ++i)
	for(j=0; j<tmap[0].length; ++j) tmap[i][j] = map[i+1][j+1];
	}//else
	
	
	try {
	wr.writeByte( (byte) tmap.length); //2. write the first 4 variables: size of stage and position of Sikla
	wr.writeByte( (byte) tmap[0].length);
	
	i = (byte) ((delborders)? -1 : 0);
	wr.writeByte(pos[0]+i); wr.writeByte(pos[1]+i);
	
	for (; v<5; ++v) {
	for(j=0; j<tmap[0].length; ++j) { //rows
	for(i=0; i<tmap.length; ++i) {
	if (tmap[i][j]==v) { //found!
	
	//3. extend square
	i2=i; j2=j;
	down = true; right = true; corner = true;
	
	while (down && right && corner) {
	i2++; j2++;
	//try to exrtend down
	if (j2==tmap[0].length) {down=false;}
	else {
	for(k=i; k<i2; ++k)
	if (tmap[k][j2]!=v) {down=false; break;}
	}//else
	
	//try to exrtend right
	if (i2==tmap.length) {right=false;}
	else {
	for(k=j; k<j2; ++k)
	if (tmap[i2][k]!=v) {right=false; break;}
	}//else
	
	//check corner
	if (down && right && tmap[i2][j2]!=v) corner = false;
	}// while
	
	//4. extend best way after square breaks
	
	//4a. if right was fitting, but down wasn't fitting
	if (!down && right) {
	while (right) {
	if (i2==tmap.length) {right=false;}
	else {
	for(k=j; k<j2; ++k)
	if (tmap[i2][k]!=v) {right=false; break;}
	}//else
	
	i2++;
	}//while
	i2--;
	}//if !down && right
	
	//4b. if down was fitting, but right wasn't fitting
	else if (!right && down) {
	while (down) {
	if (j2==tmap[0].length) {down=false;}
	else {
	for(k=i; k<i2; ++k)
	if (tmap[k][j2]!=v) {down=false; break;}
	}//else
	
	j2++;
	}//while
	j2--;
	}//else if !right && down
	
	//4c. corner wasn't fittng, but down and right was fitting. Here it's more compicated scenario...
	else if(!corner && down && right) {
	
	//what happens if you extend down
	j3=j2;
	while (down) {
	if (j3==tmap[0].length) {down=false;}
	else {
	for(k=i; k<i2; ++k)
	if (tmap[k][j3]!=v) {down=false; break;}
	}//else
	
	j3++;
	}//while
	
	//what happens if you extend right
	i3=i2;
	while (right) {
	if (i3==tmap.length) {right=false;}
	else {
	for(k=j; k<j2; ++k)
	if (tmap[i3][k]!=v) {right=false; break;}
	}//else
	
	i3++;
	}//while
	
	i3--; j3--;
	
	//what was better?
	if ( (i2-i)*(j3-j)>(i3-i)*(j2-j) ) {j2=j3;}
	else {i2=i3;}
	}//else if !corner && down && right
	
	//write the result and delete the elements from tmap
	if (i2==i+1 && j2==j+1) {wr.writeByte(i); wr.writeByte(j); tmap[i][j]=0;} //single square
	
	else if (j2==j+1) { //horizontal line
	wr.writeByte(-2); wr.writeByte(i); wr.writeByte(i2-1); //-2 means you will read two numbers. from...to
	wr.writeByte(j);
	for (i3=i; i3<i2; i3++) tmap[i3][j]=0;
	}//else if
	
	else if (i2==i+1) { //vertical line
	wr.writeByte(i);
	wr.writeByte(-2); wr.writeByte(j); wr.writeByte(j2-1);
	for (j3=j; j3<j2; j3++) tmap[i][j3]=0;
	}//else if
	
	else { //box
	wr.writeByte(-2); wr.writeByte(i); wr.writeByte(i2-1);
	wr.writeByte(-2); wr.writeByte(j); wr.writeByte(j2-1);
	for (i3=i; i3<i2; i3++)
	for (j3=j; j3<j2; j3++) tmap[i3][j3]=0;
	}//else

	}//if v
	}//for i
	}//for j
	
	if (v<4) wr.writeByte(-3);
	else {wr.writeByte(-3); wr.writeByte(-1);}
	}//for v

	}//try
	catch (Exception ex) {System.out.print(ex.toString()+"\n"); return 1;}
	return 0;
}//WRITE_MAP



static int loadStage(SiklaMain sm, int i, boolean pers, boolean gp) {
	//i can also be -1, so this method will load the last stage.
	int c=0;
	DataInputStream sc = null;
	
	try {
	sc = new DataInputStream(new FileInputStream( (pers)? sm.pf : SiklaMain.fmain));
	}//try
	catch (java.io.FileNotFoundException ex) {return 1;}

	if (i==-1) {
	if(!pers) i = numOfStages(SiklaMain.fmain);
	else i = numOfStages(sm.pf);
	}// if i==-1

	skipStages(sc,null,i-1); //read it!
	Object ob[] = readMap(sc,gp);
	
	
	if (ob==null) return -1;
	if (gp) sm.gp.setStage(i, pers, ob);
	else sm.cr.setStage(i, pers, ob);
	
	try { sc.close(); }
	catch (java.io.IOException ex) {}
	return 0;
}//LOAD_STAGE



static int loadGame(SiklaMain sm, String prof) {

	//returns 0 if read info and a map. 1 if read only info, so loads last stage
	int l1,l2,i,j, gls=0, blks=0;
	long l;
	File tf;
	
	try {
	//1. check that profile is chosen
	if (prof.equals("")) {
	JOptionPane.showMessageDialog(null,"Δεν έχετε διαλέξει προφίλ.","Σφάλμα",JOptionPane.WARNING_MESSAGE);
	return 1;
	}//if
	tf = new File("sikla/profiles/"+prof+"/data");
	l = tf.length();
	DataInputStream sc = new DataInputStream (new FileInputStream (tf) );
	sm.profile=prof;
	SiklaMain.pf = new File("sikla/profiles/"+prof+"/stages");
	
	
	//2.check that data file is not empty...
	if (l==0) { sm.setDefaultValues(prof, true); return loadStage(sm,1,false,true); }

	
	//3. Set avstages, and cstage-pmode-fload variables
	sm.avstages = sc.readInt();
	sm.cstage = sc.readInt();
	if (sm.cstage<0) {sm.pmode=true; sm.loadf=sm.pf; sm.cstage*=-1;}
	else {sm.pmode=false; sm.loadf=SiklaMain.fmain;}
	
	
	sm.setDefaultValues(prof,false);
	//4. Load map if any...
	if (l==8) {sm.gp.moves=0; return loadStage(sm, -1, false, true);}

	
	//else...
	sm.gp.moves = sc.readInt(); //set moves
	
	Object ob[] = readMap(sc,true);
	if (ob==null) return loadStage(sm, -1, false, true);
	
	sm.gp.map = (byte[][]) ob[0];
	byte tpos[] = (byte[]) ob[1];
	sm.gp.posx = tpos[0]; sm.gp.posy = tpos[1];
	sm.gp.gnum = (Byte) ob[2];
	
	sm.setMode(SiklaMain.PLAY);
	sm.gp.repaint();
	j=0; //return value

	}//try
	catch (java.io.EOFException ex) {j=1;}
	catch (java.io.IOException ex) {ex.printStackTrace(); j=1;}
	
	for (i=1; i<6; i++) sm.mi[i].setEnabled(true);
	return j;
}//LOAD_GAME



static int deleteStage(SiklaMain sm, File f, int num) {

	if (sm.pers==0) sm.pers = numOfStages(f);
	
	if (num==0) {
	JOptionPane.showMessageDialog(null, "Το επίπεδο δεν είναι ακόμα αποθηκευμένο.", "Διαγραφή", JOptionPane.PLAIN_MESSAGE);
	return 0;
	}//if
	
	File tmp = new File(".temp");
	try {
	tmp.createNewFile();
	}//try
	catch (java.io.IOException ex) {return 1;}
	
	int status;
	DataInputStream sc = null;
	DataOutputStream wr = null;
	
	try {
	sc = new DataInputStream(new FileInputStream(f));
	wr = new DataOutputStream(new FileOutputStream(tmp,false));
	
	status = skipStages(sc,wr,num-1); //read and write
	if (status==0) status = skipStages(sc, null, 1); // read but not write
	if (status==0) status = skipStages(sc,wr,-1); //write the rest
	else return -1;
	
	wr.close();
	sc.close();
	if (status==-1) {sm.pers--; sm.crstage=0; f.delete(); tmp.renameTo(f);}
	}//try
	catch (java.io.IOException ex) { return 1;}
	
	JOptionPane.showMessageDialog(null, "Το επίπεδο διεγράφη!", "Διαγραφή", JOptionPane.PLAIN_MESSAGE);
	return 0;
}//DELETE_STAGE


static int saveStage(StageCreator cr) {
	
	if (cr.par.pers==0) cr.par.pers = numOfStages(cr.par.pf);
	
	if (cr.spos[0]==-1 || cr.spos[1]==-1) {
	JOptionPane.showMessageDialog(null, "Παρακαλώ τοποθετήστε κάπου τον Sikla!", "Προσοχή", JOptionPane.PLAIN_MESSAGE);
	return 0;
	}//if
	
	boolean replace = false;
	int status, num = cr.par.crstage;
	DataOutputStream wr = null;
	
	if (num!=0 && cr.par.crpmode) {
	int ch = JOptionPane.showOptionDialog(null, "Θέλετε να αποθηκεύσετε το επίπεδο ως νέο ή να αντικαταστήσετε;",
		"Αποθήκευση", JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
		null, new Object[] {"Νέο", "Αντικατάσταση"}, null);
		
	if (ch==JOptionPane.CLOSED_OPTION) return 0;
	replace = ch==1;
	}//if
	
	
	
	if (!replace) { //just put to the end...
	try {
	wr = new DataOutputStream( new FileOutputStream(cr.par.pf, true));
	status = writeMap(wr, cr.getActiveMap(), cr.spos, false);
	if (status==0) {cr.par.pers++; cr.par.crstage=cr.par.pers;}
	wr.close();
	}//try
	catch (java.io.IOException ex) {ex.printStackTrace(); return 1;}
	}//if append
	
	else {
	try {
	File tmp = new File(".temp"); tmp.createNewFile();
	
	wr = new DataOutputStream( new FileOutputStream(tmp, false));
	DataInputStream sc = new DataInputStream( new FileInputStream(cr.par.pf));
	
	
	status = skipStages(sc,wr,num-1); //read and write
	if (status==0) status = skipStages(sc, null, 1); // read but not write
	if (status==0) status = writeMap(wr, cr.getActiveMap(), cr.spos, false); //write the new one
	if (status==0) status = skipStages(sc,wr,-1); //write the rest
	
	
	wr.close();
	sc.close();
	if (status==-1) {cr.par.pf.delete(); tmp.renameTo(cr.par.pf);}
	}//try
	catch (java.io.IOException ex) {ex.printStackTrace(); return 1;}
	}//else

	
	JOptionPane.showMessageDialog(null,"Το επίπεδο αποθηκεύθηκε.","Αποθευση",JOptionPane.PLAIN_MESSAGE);
	return 0;
	
}//SAVE_STAGE

}//class
