package application;

import model.dao.FactoryDao;
import model.dao.SellerDao;
import model.dao.impl.SellerDaoJDBC;
import model.date.DateFormat;
import model.entities.Department;
import model.entities.Seller;


import java.util.Date;
import java.util.List;

public class Program {
    public static void main(String[] args) {
        System.out.println("=== TEST 1 : findById ===");
        SellerDao sellerDao = FactoryDao.createSellerDao();
        Seller obj = sellerDao.findById(1);
        System.out.println(obj);

        System.out.println("\n=== TEST 2 : findByDepartment ===");
        List<Seller> sellers = sellerDao.findByDepartment(new Department(1,"Teste"));
        sellers.forEach(System.out::println);

        System.out.println("\n=== TEST 3 : findAll ===");
        sellers = sellerDao.findAll();
        sellers.forEach(System.out::println);

        System.out.println("\n=== TEST 4 : insert ===");
        Seller seller = new Seller("Roberto S",null,new Date("29/11/1999"),"roberto@gmail.com",12000.00,
                new Department(2,null));
        sellerDao.insert(seller);
        System.out.printf("Done seller: %s cadastred on id: %d",seller.getName(),seller.getId());

        System.out.println("\n=== TEST 5 : insert ===");
        seller = sellerDao.findById(1);
        seller.setName("Teste");
        sellerDao.update(seller);

        System.out.println("\n=== TEST 5 : deleteByID ===");
        sellerDao.deleteById(17);

    }
}
