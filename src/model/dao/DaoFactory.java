package model.dao;

import dbase.DBASE;
import model.dao.implement.DepartmentDaoJDBC;
import model.dao.implement.SellerDaoJDBC;

public class DaoFactory {
	
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DBASE.getConnection());
	}
	
	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DBASE.getConnection());
	}

}
 