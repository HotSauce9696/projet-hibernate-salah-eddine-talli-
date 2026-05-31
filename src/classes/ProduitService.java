/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import fichierhibernate.NewHibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Query;  
import java.util.List;

public class ProduitService {
    
    
    public static Session session;
    
    public static void openSession() {
        SessionFactory sessionFactory = NewHibernateUtil.getSessionFactory();
        session = sessionFactory.openSession();
        System.out.println("Session opened");
    }
    
    public static void closeSession() {
        if (session != null && session.isOpen()) {
            session.close();
            System.out.println("Session closed");
        }
    }
    
    
    public static int ajouterProduit(Produit p) {
        Transaction tx = null;
        Session localSession = null;
        try {
            localSession = NewHibernateUtil.getSessionFactory().openSession();
            tx = localSession.beginTransaction();
            int ID = (Integer) localSession.save(p);
            tx.commit();
            System.out.println("Produit créé avec succès! ID: " + ID);
            return ID;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error adding product: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            if (localSession != null && localSession.isOpen()) {
                localSession.close();
            }
        }
    }
    
    
    public static List<Produit> getAllProduits() {
        Session localSession = null;
        try {
            localSession = NewHibernateUtil.getSessionFactory().openSession();
            Query query = localSession.createQuery("FROM Produit ORDER BY id");
            List<Produit> produits = query.list();
            System.out.println("Found " + produits.size() + " produits");
            return produits;
        } catch (Exception e) {
            System.err.println("Error getting products: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (localSession != null && localSession.isOpen()) {
                localSession.close();
            }
        }
    }
    
    
    public static List<Produit> getProduitById(String code) {
        Session localSession = null;
        try {
            localSession = NewHibernateUtil.getSessionFactory().openSession();
            String hql = "FROM Produit WHERE code = :code";
            Query query = localSession.createQuery(hql);
            query.setParameter("code", code);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error getting product by code: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (localSession != null && localSession.isOpen()) {
                localSession.close();
            }
        }
    }
    
    
    public static void modifierProduit(Produit p) {
        Transaction tx = null;
        Session localSession = null;
        try {
            localSession = NewHibernateUtil.getSessionFactory().openSession();
            tx = localSession.beginTransaction();
            
            String hql = "FROM Produit WHERE code = :code";
            Query query = localSession.createQuery(hql);
            query.setParameter("code", p.getCode());
            Produit existingProduct = (Produit) query.uniqueResult();
            
            if (existingProduct != null) {
                existingProduct.setLibelle(p.getLibelle());
                existingProduct.setType(p.getType());
                existingProduct.setQuantiteEnStock(p.getQuantiteEnStock());
                existingProduct.setDisponibilite(p.isDisponibilite());
                
                localSession.update(existingProduct);
                tx.commit();
                System.out.println("Produit modifié avec succès: " + existingProduct);
            } else {
                System.out.println("Produit avec code " + p.getCode() + " non trouvé!");
                tx.rollback();
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error modifying product: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (localSession != null && localSession.isOpen()) {
                localSession.close();
            }
        }
    }
    
    
    public static void supprimerProduit(int id) {
        Transaction tx = null;
        Session localSession = null;
        try {
            localSession = NewHibernateUtil.getSessionFactory().openSession();
            tx = localSession.beginTransaction();
            Produit p = (Produit) localSession.get(Produit.class, id);
            if (p != null) {
                localSession.delete(p);
                tx.commit();
                System.out.println("Produit supprimé avec succès! ID: " + id);
            } else {
                System.out.println("Produit non trouvé avec ID: " + id);
                tx.rollback();
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (localSession != null && localSession.isOpen()) {
                localSession.close();
            }
        }
    }
    
    
    public static List<String> getDistinctTypes() {
        Session localSession = null;
        try {
            localSession = NewHibernateUtil.getSessionFactory().openSession();
            Query query = localSession.createQuery("SELECT DISTINCT type FROM Produit ORDER BY type");
            List<String> types = query.list();
            System.out.println("Found " + types.size() + " distinct types");
            
            if (types.isEmpty()) {
                types.add("Fruits");
                types.add("Légumes");
                types.add("Produits laitiers");
                types.add("Boulangerie");
                types.add("Boissons");
            }
            return types;
        } catch (Exception e) {
            System.err.println("Error getting distinct types: " + e.getMessage());
            e.printStackTrace();
            List<String> defaultTypes = new java.util.ArrayList<>();
            defaultTypes.add("Fruits");
            defaultTypes.add("Légumes");
            defaultTypes.add("Produits laitiers");
            defaultTypes.add("Boulangerie");
            defaultTypes.add("Boissons");
            return defaultTypes;
        } finally {
            if (localSession != null && localSession.isOpen()) {
                localSession.close();
            }
        }
    }
    
    
    public static List<Produit> rechercherProduits(String keyword) {
        Session localSession = null;
        try {
            localSession = NewHibernateUtil.getSessionFactory().openSession();
            Query query = localSession.createQuery(
                "FROM Produit WHERE libelle LIKE :keyword OR type LIKE :keyword ORDER BY id"
            );
            query.setParameter("keyword", "%" + keyword + "%");
            List<Produit> results = query.list();
            System.out.println("Search found " + results.size() + " products for keyword: " + keyword);
            return results;
        } catch (Exception e) {
            System.err.println("Error searching products: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (localSession != null && localSession.isOpen()) {
                localSession.close();
            }
        }
    }
}