<%-- 
    Document   : api
    Created on : Sep 30, 2015, 12:07:57 PM
    Author     : Alex
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>BCCH API</title>
    </head>
    <body>
        <div>
            <h1>Log In</h1>
            <p>URL : /api?cmd=login<br/>Method : POST / GET</p>
            <form action="/api?cmd=login" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Get Item From Isbn</h1>
            <p>URL : /api?cmd=getItemFromIsbn<br/>Method : POST / GET</p>
            <form action="/api?cmd=getItemFromIsbn" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>isbn</td>
                        <td><input type="text" name="isbn"/> : string</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Get Customers</h1>
            <p>URL : /api?cmd=getCustomers<br/>Method : POST / GET</p>
            <form action="/api?cmd=getCustomers" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Create an order</h1>
            <p>URL : /api?cmd=createOrder<br/>Method : POST / GET</p>
            <form action="/api?cmd=createOrder" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>order</td>
                        <td><input type="text" name="order"/> : json string</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Get Vendors</h1>
            <p>URL : /api?cmd=getVendors<br/>Method : POST / GET</p>
            <form action="/api?cmd=getVendors" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Create Receiving</h1>
            <p>URL : /api?cmd=createReceiving<br/>Method : POST / GET</p>
            <form action="/api?cmd=createReceiving" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>receiving</td>
                        <td><input type="text" name="receiving" /> : json string</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Get Receivings</h1>
            <p>URL : /api?cmd=getReceivings<br/>Method : POST / GET</p>
            <form action="/api?cmd=getReceivings" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>page</td>
                        <td><input type="text" name="page" /> : 1-based page num: long</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Get Receiving Items</h1>
            <p>URL : /api?cmd=getReceivingItems<br/>Method : POST / GET</p>
            <form action="/api?cmd=getReceivingItems" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>rid</td>
                        <td><input type="text" name="rid" /> : receiving id : integer</td>
                    </tr>
                    <tr>
                        <td>page</td>
                        <td><input type="text" name="page" /> : zero-based index : integer</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Update Receiving</h1>
            <p>URL : /api?cmd=updateReceiving<br/>Method : POST / GET</p>
            <form action="/api?cmd=updateReceiving" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>rid</td>
                        <td><input type="text" name="rid" /> : receiving id : integer</td>
                    </tr>
                    <tr>
                        <td>po_number</td>
                        <td><input type="text" name="po_number" /> : string</td>
                    </tr>
                    <tr>
                        <td>po_date</td>
                        <td><input type="text" name="po_number" /> : date</td>
                    </tr>
                    <tr>
                        <td>vendor</td>
                        <td><input type="text" name="vendor" /> : integer</td>
                    </tr>
                    <tr>
                        <td>publisher</td>
                        <td><input type="text" name="publisher" /> : string</td>
                    </tr>
                    <tr>
                        <td>comment</td>
                        <td><input type="text" name="comment" /> : string</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Update Receiving Item</h1>
            <p>URL : /api?cmd=updateReceivingItem<br/>Method : POST / GET</p>
            <form action="/api?cmd=updateReceivingItem" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>rid</td>
                        <td><input type="text" name="rid" /> : receiving id : long</td>
                    </tr>
                    <tr>
                        <td>received_quantity</td>
                        <td><input type="text" name="received_quantity" /> : integer</td>
                    </tr>
                    <tr>
                        <td>ordered_quantity</td>
                        <td><input type="text" name="ordered_quantity" /> : integer</td>
                    </tr>
                    <tr>
                        <td>percentage_list</td>
                        <td><input type="text" name="percentage_list" /> : float</td>
                    </tr>
                    <tr>
                        <td>cost_per_lb</td>
                        <td><input type="text" name="cost_per_lb" /> : float</td>
                    </tr>
                    <tr>
                        <td>cost</td>
                        <td><input type="text" name="cost" /> : float</td>
                    </tr>
                    <tr>
                        <td>isbn</td>
                        <td><input type="text" name="isbn" /> : string</td>
                    </tr>
                    <tr>
                        <td>cond</td>
                        <td><input type="text" name="cond" /> : string</td>
                    </tr>
                    <tr>
                        <td>title</td>
                        <td><input type="text" name="title" /> : string</td>
                    </tr>
                    <tr>
                        <td>bin</td>
                        <td><input type="text" name="bin" /> : string</td>
                    </tr>
                    <tr>
                        <td>list_price</td>
                        <td><input type="text" name="list_price" /> : float</td>
                    </tr>
                    <tr>
                        <td>selling_price</td>
                        <td><input type="text" name="selling_price" /> : float</td>
                    </tr>
                    <tr>
                        <td>cover</td>
                        <td><input type="text" name="cover" /> : string</td>
                    </tr>
                    <tr>
                        <td>bell_book</td>
                        <td><input type="text" name="bell_book" /> : true/false</td>
                    </tr>
                    <tr>
                        <td>break_room</td>
                        <td><input type="text" name="break_riom" /> : true/false</td>
                    </tr>
                    <tr>
                        <td>higher_education</td>
                        <td><input type="text" name="higher_education" /> : true/false</td>
                    </tr>
                    <tr>
                        <td>restricted</td>
                        <td><input type="text" name="restricted" /> : true/false</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Update Inventory Item</h1>
            <p>URL : /api?cmd=updateInventoryItem<br/>Method : POST</p>
            <form action="/api?cmd=updateInventoryItem" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>id</td>
                        <td><input type="text" name="id" /> : inventory item id : long</td>
                    </tr>
<!--                    <tr>
                        <td>isbn</td>
                        <td><input type="text" name="isbn" /> : string</td>
                    </tr>
                    <tr>
                        <td>cond</td>
                        <td><input type="text" name="cond" /> : string</td>
                    </tr>
                    <tr>
                        <td>title</td>
                        <td><input type="text" name="title" /> : string</td>
                    </tr>
                    <tr>
                        <td>author</td>
                        <td><input type="text" name="author" /> : string</td>
                    </tr>
                    <tr>
                        <td>publisher</td>
                        <td><input type="text" name="publisher" /> : string</td>
                    </tr>
                    <tr>
                        <td>list_price</td>
                        <td><input type="text" name="list_price" /> : float</td>
                    </tr>
                    <tr>
                        <td>selling_price</td>
                        <td><input type="text" name="selling_price" /> : float</td>
                    </tr>
                    <tr>
                        <td>on_hand</td>
                        <td><input type="text" name="on_hand" /> : integer</td>
                    </tr>-->
                    <tr>
                        <td>bin</td>
                        <td><input type="text" name="bin" /> : string</td>
                    </tr>
<!--                    <tr>
                        <td>cover</td>
                        <td><input type="text" name="cover" /> : string</td>
                    </tr>
                    <tr>
                        <td>bell_book</td>
                        <td><input type="text" name="bell_book" /> : true/false</td>
                    </tr>
                    <tr>
                        <td>break_room</td>
                        <td><input type="text" name="break_riom" /> : true/false</td>
                    </tr>
                    <tr>
                        <td>higher_education</td>
                        <td><input type="text" name="higher_education" /> : true/false</td>
                    </tr>
                    <tr>
                        <td>restricted</td>
                        <td><input type="text" name="restricted" /> : true/false</td>
                    </tr>
                    <tr>
                        <td>biblio</td>
                        <td><input type="text" name="biblio" /> : string</td>
                    </tr>
                    <tr>
                        <td>category</td>
                        <td><input type="text" name="category" /> : string</td>
                    </tr>
                    <tr>
                        <td>number_of_pages</td>
                        <td><input type="text" name="number_of_pages" /> : integer</td>
                    </tr>
                    <tr>
                        <td>length</td>
                        <td><input type="text" name="length" /> : float</td>
                    </tr>
                    <tr>
                        <td>width</td>
                        <td><input type="text" name="width" /> : float</td>
                    </tr>
                    <tr>
                        <td>height</td>
                        <td><input type="text" name="height" /> : float</td>
                    </tr>
                    <tr>
                        <td>weight</td>
                        <td><input type="text" name="weight" /> : float</td>
                    </tr>-->
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <h1>Get InventoryItem History</h1>
            <p>URL : /api?cmd=getInventoryItemHistory<br/>Method : POST / GET</p>
            <form action="/api?cmd=getInventoryItemHistory" method="POST">
                <table>
                    <tr>
                        <td>name</td>
                        <td><input type="text" name="name"/> : string</td>
                    </tr>
                    <tr>
                        <td>password</td>
                        <td><input type="text" name="password"/> : string</td>
                    </tr>
                    <tr>
                        <td>id</td>
                        <td><input type="text" name="id"/> : long</td>
                    </tr>
                    <tr>
                        <td><input type="submit"/></td><td></td>
                    </tr>
                </table>
            </form>
        </div>
    </body>
</html>
