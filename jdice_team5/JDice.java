import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
/**
 * JDice: Chương trình Java minh họa việc tung xúc xắc với giao diện đồ họa.
 * <p>
 * Lý do refactor:
 * - Sửa lỗi logic xử lý sự kiện trùng lặp do JComboBox sinh hai lần ActionEvent cho cùng một thao tác.
 * - Đổi tên biến, tách phần xử lý sự kiện vào lớp con JDiceListener để dễ quản lý và mở rộng.
 * - Cấu trúc lại phương thức main cho rõ ràng: tách khởi tạo giao diện, đăng ký listener, thiết lập JFrame.
 * - Thêm xử lý lỗi khi chuỗi nhập vào không đúng định dạng xúc xắc (hiện thông báo qua showError).
 * </p>
 *
 * <p>
 * Chức năng chính:
 * - Cho phép người dùng nhập hoặc chọn biểu thức xúc xắc (ví dụ “3d6+2”) trong JComboBox có thể chỉnh sửa.
 * - Khi nhấn “Roll Selection” hoặc các nút d4, d6… sẽ phân tích biểu thức bằng DiceParser và sinh kết quả cho từng xúc xắc.
 * - Kết quả hiển thị trong JList, cho phép xóa toàn bộ bằng nút “Clear”.
 * - Hỗ trợ đọc danh sách biểu thức xúc xắc từ tệp đầu vào khi truyền tên file qua args.
 * </p>
* Bổ sung chức năng cho showError

*/
public class JDice {
    static final String CLEAR="Clear";
    static final String ROLL="Roll Selection";
	/**
	 * Hiển thị hộp thoại cảnh báo lỗi cho người dùng
	 * @param message Thông điệp lỗi sẽ được hiện lên dialog
	 */
    static void showError(String s) {
		JOptionPane.showMessageDialog(null, s, "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
    private static class JDiceListener implements ActionListener {
	Vector<String> listItems;
	JList resultList;
	JComboBox inputBox;
	long lastEvent; /* hack to prevent double events with text entry */

	public JDiceListener(JList resultList, JComboBox inputBox){
	    this.listItems = new Vector<String>();
	    this.resultList = resultList;
	    this.inputBox = inputBox;
	    lastEvent = 0;
	}
	public void actionPerformed(ActionEvent e) {

	    if(e.getWhen()==lastEvent) return;
	    lastEvent=e.getWhen();
		
	    if(e.getSource() instanceof JComboBox || e.getActionCommand().equals(ROLL)) {
			String s=inputBox.getSelectedItem().toString();
			String[] arr=s.split("=");
			String name="";
			for(int i=0;i<arr.length-2;i++) { 
		    	name=arr[i]+"=";
			}
			if(arr.length>=2){
		    	name=name+arr[arr.length-2];
				doRoll(name,arr[arr.length-1]);
			}
	    }
	    else if(e.getActionCommand().equals(CLEAR)){ 
			doClear();
	    }
	    else {
			doRoll(null,e.getActionCommand());
	    }
	}
	private void doClear(){
	    resultList.clearSelection();
	    listItems.clear();
	    resultList.setListData(listItems);
	}
	private void doRoll(String name, String diceString) {
	    String prepend="";
	    int start=0;
	    int i;
	    Vector<DieRoll> v=DiceParser.parseRoll(diceString);
	    if(v==null) {
			showError("Invalid dice string" +diceString);
			return;
	    }
	    if(name!=null) {
			listItems.add(0,name);
			start=1;
			prepend="  ";
	    }
	    int[] selectionIndices = new int[start+v.size()];
	    for(i=0;i<v.size();i++) {
			DieRoll dr=v.get(i);
			RollResult rr=dr.makeRoll();
			String toAdd=prepend+dr+"  =>  "+rr;
			listItems.add(i+start,toAdd);
	    }
	    for(i=0;i<selectionIndices.length;i++) {
			selectionIndices[i]=i;
	    }
	    resultList.setListData(listItems);
	    resultList.	setSelectedIndices(selectionIndices);
	}


    }
    public static void main(String[] args) {
		Vector<String> v=new Vector<String>();
		if(args.length>=1) {
	    	try {
				BufferedReader br=new BufferedReader(new FileReader(args[0]));
				String s;
				while((s=br.readLine())!=null) {
		    		v.add(s);
				}
	    	}
	    	catch(IOException ioe){
				ioe.printStackTrace();
				System.err.println("***********\n**********\n");
				System.err.println("Could not read input file: "+args[0]);
				System.err.println("***********\n**********\n");
	    	}

		}
		JFrame jf=new JFrame("Dice Roller");
		Container c=jf.getContentPane();
		c.setLayout(new BorderLayout());
		JList jl=new JList();
		c.add(jl,BorderLayout.CENTER);
		JComboBox jcb=new JComboBox(v);
		jcb.setEditable(true);
		c.add(jcb,BorderLayout.NORTH);
		JDiceListener jdl=new JDiceListener(jl,jcb);
		jcb.addActionListener(jdl);
		JPanel rightSide=new JPanel();
		rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
		String[] buttons={ROLL,
			  "d4",
			  "d6",
			  "d8",
			  "d10",
			  "d12",
			  "d20",
			  "d100",
			  CLEAR};
		for(int i=0;i<buttons.length;i++) {
	    	JButton newButton=new JButton(buttons[i]);
	    	rightSide.add(newButton);
	    	newButton.addActionListener(null);
		}
		c.add(rightSide,BorderLayout.EAST);
		jf.setSize(450,500);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true); 
    }
}
