package edu.kh.emp.run;

import edu.kh.emp.view.EmployeeView;

// 실행용 클래스
public class EmployeeRun {
	public static void main(String[] args) {
		
		// 기존방식 EmployeeView empView = newEmployeeView();		
		new EmployeeView().displayMenu();
	}

}
