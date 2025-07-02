package application;

import model.dao.FactoryDao;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;


import java.util.Date;

public class Program {
    public static void main(String[] args) {

        SellerDao sellerDao = FactoryDao.createSellerDao();
        Seller obj = sellerDao.findById(1);
        System.out.println(obj);
    }
}
