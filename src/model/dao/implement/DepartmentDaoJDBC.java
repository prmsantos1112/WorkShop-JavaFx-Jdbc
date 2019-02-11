package model.dao.implement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

import dbase.DBASE;
import dbase.DbException;
import dbase.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection connection;

	public DepartmentDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Department objectiv) {

		PreparedStatement prepraredStat = null;

		try {
			prepraredStat = connection.prepareStatement("INSERT INTO department " + "(Name) " + "VALUES " + "(?)",
					Statement.RETURN_GENERATED_KEYS);

			prepraredStat.setString(1, objectiv.getName());

			int rowsAffected = prepraredStat.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet resultSet = prepraredStat.getGeneratedKeys();
				if (resultSet.next()) {
					int id = resultSet.getInt(1);
					objectiv.setId(id);
				}
			} else {
				throw new DbException("Unexpected Error !! No rows Affected !!");

			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DBASE.closeStatement(prepraredStat);
		}
	}

	@Override
	public void update(Department objectiv) {

		PreparedStatement prepraredStat = null;

		try {
			prepraredStat = connection.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");

			prepraredStat.setString(1, objectiv.getName());
			prepraredStat.setInt(2, objectiv.getId());

			prepraredStat.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DBASE.closeStatement(prepraredStat);

		}
	}

	@Override

	public void deleteById(Integer id) {

		PreparedStatement prepraredStat = null;

		try {
			prepraredStat = connection.prepareStatement(
				  "DELETE FROM department WHERE Id = ?");

			prepraredStat.setInt(1, id);
			prepraredStat.executeUpdate();

		}

		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} 

		finally {
			DBASE.closeStatement(prepraredStat);
		}
	}

	@Override

	public Department findById(Integer id) {

		PreparedStatement prepraredStat = null;
		ResultSet resultSet = null;

		try {
			prepraredStat = connection.prepareStatement("SELECT * FROM department WHERE Id = ?");

			prepraredStat.setInt(1, id);
			resultSet = prepraredStat.executeQuery();

			if (resultSet.next()) {
				Department objectiv = new Department();
				objectiv.setId(resultSet.getInt("Id"));
				objectiv.setName(resultSet.getString("Name"));
				return objectiv;

			}
			return null;
		}

		catch (SQLException e) {

			throw new DbException(e.getMessage());
		}

		finally {

			DBASE.closeStatement(prepraredStat);
			DBASE.closeResultSet(resultSet);
		}

	}

	@Override

	public List<Department> findAll() {

		PreparedStatement prepraredStat = null;
		ResultSet resultSet = null;

		try {
			prepraredStat = connection.prepareStatement(
					"SELECT * FROM department ORDER BY Name");

			resultSet = prepraredStat.executeQuery();
			List<Department> list = new ArrayList<>();

			while (resultSet.next()) {
				Department objectiv = new Department();
				objectiv.setId(resultSet.getInt("Id"));
				objectiv.setName(resultSet.getString("Name"));
				list.add(objectiv);

			}
			return list;

		}		
		catch (SQLException e) {

			throw new DbException(e.getMessage());
		}

		finally {
			DBASE.closeStatement(prepraredStat);
			DBASE.closeResultSet(resultSet);
		}
	}
}
