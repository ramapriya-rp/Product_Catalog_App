Backend code: Java Springboot using maven.  
Frontend code: React and Typescript   

Execution steps:  
Download the folders from   https://github.com/ramapriya-rp/Product_Catalog_App.git  
folder - "product_catalog" is for Frontend  
folder - "productcatalog" is for Backend  

To execute Backend:  
1. Open cmd and navigate to the folder "productcatalog". (i.e current directory should be: productcatalog)  
2. Type java -jar productcatalog-1.0.0.jar  
3. The backend application will get started.  
4. It will load 14 products.  
5. To modify the product data (if desired): modify the file "products.csv" under productcatalog\src\main\resources\. and restart the server. (java -jar productcatalog-1.0.0.jar).  

6. Backend application will be hosted at: http://localhost:8080   
7. To add products:- POST: http://localhost:8080/product  (product object. Except 'id; all fields are mandatory. ID is auto-generated  by the system)    
8. To retrieve products:- GET: http://localhost:8080/products or http://localhost:8080/products?pageNo=0&pageSize=5  (pageno and pagesize are optional params, if not given, default values are taken from property file).  
9. To retrieve a product based on ID:- GET: http://localhost:8080/product/5     
10. To retrieve products matching based on a given name:- GET: http://localhost:8080/search?query=ikea (implemented fuzzy logic and cache)      

11. Logs will be generated in logs folder.  
   
To execute Frontend:  
1. Open cmd and navigate to the folder product_catalog. (i.e current directory should be: product_catalog)  
2. Type npm install (to install the dependencies)  
3. Type npm start (to start the server)  
4. Application will be hosted  at http://localhost:3000/  



