package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import javax.naming.Name;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SellerDaoJDBC implements SellerDao {
    private Connection conn = null;
    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller seller) {

    }

    @Override
    public void update(Seller seller) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT seller.*,department.Name as DepName " +
                            "From seller INNER JOIN department " +
                            "ON seller.DepartmentId = department.Id " +
                            "Where seller.Id = ?;");
            ps.setInt(1,id);
            rs = ps.executeQuery();
            if (rs.next()){
                Department dep = initiateDep(rs);
                Seller obj =initiateSeller(rs,dep);
                return obj;
            }
            return null;
        }catch (SQLException e){
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }

    }

    private Seller initiateSeller(ResultSet rs,Department dep) throws SQLException {
        Seller obj = new Seller();
        obj.setName(rs.getString("Name"));
        obj.setId(rs.getInt("Id"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        obj.setEmail(rs.getString("Email"));
        obj.setBaseSalary(rs.getDouble("BaseSalary"));
        obj.setDepartment(dep);
        return obj;
    };

    private Department initiateDep (ResultSet rs) throws SQLException{
            Department obj = new Department(
                rs.getInt("DepartmentID"),
                rs.getString("DepName"));
                return obj;
    }

    @Override
    public List<Seller> findAll() {
        return List.of();
    }
}
