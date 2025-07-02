package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import javax.naming.Name;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {
    private Connection conn = null;
    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller seller) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("INSERT INTO seller " +
                    "(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
                    "VALUES " +
                    "(?, ?, ?, ?, ?);",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,seller.getName());
            ps.setString(2,seller.getEmail());
            ps.setDate(3,new java.sql.Date(seller.getBirthDate().getTime()));
            ps.setDouble(4,seller.getBaseSalary());
            ps.setInt(5,seller.getDepartment().getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0){
                rs = ps.getGeneratedKeys();
                while (rs.next()){
                    seller.setId(rs.getInt(1));
                }
            }else {
                throw new SQLException("Failure on insert!");
            }

            System.out.println("Done rows affected = "+rowsAffected);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }

    }

    @Override
    public void update(Seller seller) {
        PreparedStatement ps = null;

        try {

            ps = conn.prepareStatement("UPDATE seller " +
                    "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " +
                    "WHERE Id = ?;",Statement.RETURN_GENERATED_KEYS);

            ps.setString(1,seller.getName());
            ps.setString(2,seller.getEmail());
            ps.setDate(3,new java.sql.Date(seller.getBirthDate().getTime()));
            ps.setDouble(4,seller.getBaseSalary());
            ps.setInt(5,seller.getDepartment().getId());
            ps.setInt(6,seller.getId());

            ps.executeUpdate();
            System.out.println("Done Seller updated!");

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement ps = null;

        try {

            ps = conn.prepareStatement("DELETE FROM seller\n" +
                    "WHERE Id = ?;",Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1,id);

            if (ps.executeUpdate() > 0) {
                System.out.println("Done Seller: " + id + " Dropped!");
            }else {
                System.out.println("No have seller by id: "+id);
            }


        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
        }
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

    public List<Seller> findByDepartment(Department department){

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT seller.*,department.Name as DepName " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.DepartmentId = department.Id " +
                    "WHERE DepartmentId = ? " +
                    "ORDER BY Name");
            ps.setInt(1,department.getId());
            rs = ps.executeQuery();
            List<Seller> sellers = new ArrayList<>();
            Map<Integer,Department> map = new HashMap<>();

            while(rs.next()){
                Department mapDep = map.get(rs.getInt("DepartmentId"));
                if(mapDep == null) {
                    Department dep = initiateDep(rs);
                    map.put(rs.getInt("DepartmentId"),dep);
                    mapDep = dep;
                }
                Seller seller = initiateSeller(rs,mapDep);
                sellers.add(seller);
            }
            return sellers;

        }catch (SQLException e){
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    };

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
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT seller.*,department.Name as DepName " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.DepartmentId = department.Id " +
                    "ORDER BY Name");

            rs = ps.executeQuery();
            List<Seller> sellers = new ArrayList<>();
            Map<Integer,Department> map = new HashMap<>();

            while(rs.next()){
                Department mapDep = map.get(rs.getInt("DepartmentId"));
                if(mapDep == null) {
                    Department dep = initiateDep(rs);
                    map.put(rs.getInt("DepartmentId"),dep);
                    mapDep = dep;
                }
                Seller seller = initiateSeller(rs,mapDep);
                sellers.add(seller);
            }
            return sellers;

        }catch (SQLException e){
            e.printStackTrace();
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }
}
