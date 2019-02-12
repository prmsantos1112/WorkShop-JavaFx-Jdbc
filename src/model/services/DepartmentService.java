package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao deptoDao = DaoFactory.createDepartmentDao();

	public List<Department> findAll() {
		return deptoDao.findAll();

		/*
		 * List<Department> list = new ArrayList<>(); list.add(new Department(1,
		 * "Books")); list.add(new Department(2, "Tools")); list.add(new Department(3,
		 * "Music")); list.add(new Department(4, "Shoes")); list.add(new Department(5,
		 * "Eletronics")); return list;
		 */
	}

	public void saveOrUpdate(Department object) {
		if (object.getId() == null) {
			deptoDao.insert(object);
		} else {
			deptoDao.update(object);
		}

	}

	public void remove(Department object) {
		deptoDao.deleteById(object.getId());
	}
}
