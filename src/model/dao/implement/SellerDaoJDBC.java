package model.dao.implement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Statement;

import dbase.DBASE;
import dbase.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection connection;
	
	public SellerDaoJDBC(Connection connection ) {
		this.connection = connection;
	}

	@Override
	public void insert(Seller objectiv) {
		
		PreparedStatement prepraredStat = null;
		
		try {
			prepraredStat = connection.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "  
					+ "VALUES "  
					+ "(?, ?, ?, ?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			prepraredStat.setString(1, objectiv.getName());
			prepraredStat.setString(2, objectiv.getEmail());
			prepraredStat.setDate(3, new java.sql.Date(objectiv.getBirthDate().getTime()));
			prepraredStat.setDouble(4, objectiv.getBaseSalary());
			prepraredStat.setInt(5, objectiv.getDepartment().getId());
			
			int rowsAffected = prepraredStat.executeUpdate();
			
			if (rowsAffected > 0 ) {
				ResultSet resultSet = prepraredStat.getGeneratedKeys();
				if (resultSet.next()) {
					int id = resultSet.getInt(1);
					objectiv.setId(id);
				}
				DBASE.closeResultSet(resultSet);
			}
			else {
				throw new DbException("Unexpected Error !! No rows Affected !!");
				
			}			 
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}	
		finally {
			DBASE.closeStatement(prepraredStat);
		}
		
	}

	@Override
	public void update(Seller objectiv) {
		
PreparedStatement prepraredStat = null;
		
		try {
			prepraredStat = connection.prepareStatement (
					 "UPDATE seller "  
					 + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "  
					 + "WHERE Id = ?");
			
			prepraredStat.setString(1, objectiv.getName());
			prepraredStat.setString(2, objectiv.getEmail());
			prepraredStat.setDate(3, new java.sql.Date(objectiv.getBirthDate().getTime()));
			prepraredStat.setDouble(4, objectiv.getBaseSalary());
			prepraredStat.setInt(5, objectiv.getDepartment().getId());
			prepraredStat.setInt(6, objectiv.getId());
			
			prepraredStat.executeUpdate();			
					 
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}	
		finally {
			DBASE.closeStatement(prepraredStat);
		}		
		
	}

	@Override
	public void deleteById(Integer id) {
		
		PreparedStatement prepraredStat = null;
		
		try {
			prepraredStat = connection.prepareStatement(
					"DELETE " 
					+ "FROM seller "  
					+ "WHERE Id = ?");
			
			
			prepraredStat.setInt(1, id);
			prepraredStat.executeUpdate();
			
	     }
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
        }	
		finally {
			DBASE.closeStatement(prepraredStat);
						
		}	
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement prepraredStat = null;
		ResultSet resultSet = null;
		
		try {
			prepraredStat = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName " 
					+ "FROM seller INNER JOIN department "  
					+ "ON seller.DepartmentId = department.Id "  
					+ "WHERE seller.Id = ?");
			
			prepraredStat.setInt(1, id);
			resultSet = prepraredStat.executeQuery();
			
			if (resultSet.next()) {
				Department department = instantiateDepartment(resultSet);
				
				Seller objectiv = instantiateSeller(resultSet, department);
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

	private Seller instantiateSeller(ResultSet resultSet, Department department) throws SQLException {
		
		Seller objectiv = new Seller();
		objectiv.setId(resultSet.getInt("Id"));
		objectiv.setName(resultSet.getString("Name"));
		objectiv.setEmail(resultSet.getString("Email"));
		objectiv.setBaseSalary(resultSet.getDouble("BaseSalary"));
		objectiv.setBirthDate(resultSet.getDate("BirthDate"));
		objectiv.setDepartment(department);		
		
		return objectiv;
	}

	private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
		
		Department department = new Department();
		department.setId(resultSet.getInt("DepartmentId"));
		department.setName(resultSet.getString("DepName"));
		
		return department;
	}

	@Override
	public List<Seller> findAll() {
		
		PreparedStatement prepraredStat = null;
		ResultSet resultSet = null;
		
		try {
			prepraredStat = connection.prepareStatement( 
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
			
			resultSet = prepraredStat.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (resultSet.next()) {
				
				Department depto = map.get(resultSet.getInt("DepartmentId"));
				
				if (depto == null) {
					depto = instantiateDepartment(resultSet);
					map.put(resultSet.getInt("DepartmentId"), depto);
				}
				
				Seller objectiv = instantiateSeller(resultSet, depto);
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

	@Override
	public List<Seller> findByDepartment(Department department) {
		
		PreparedStatement prepraredStat = null;
		ResultSet resultSet = null;
		
		try {
			prepraredStat = connection.prepareStatement( 
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			
			prepraredStat.setInt(1, department.getId());
			resultSet = prepraredStat.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (resultSet.next()) {
				
				Department depto = map.get(resultSet.getInt("DepartmentId"));
				
				if (depto == null) {
					depto = instantiateDepartment(resultSet);
					map.put(resultSet.getInt("DepartmentId"), depto);
				}
				
				Seller objectiv = instantiateSeller(resultSet, depto);
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
