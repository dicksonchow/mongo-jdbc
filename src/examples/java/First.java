/*
 * Copyright 2017 Carlos Tse <copperoxide@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class First {

    static void print( String name , ResultSet res )
        throws SQLException {
        System.out.println( name );
        while ( res.next() ){
            System.out.println( "\t" + res.getString( "name" ) + "\t" + res.getInt( "age" ) + "\t" + res.getObject(0) );
        }
    }

    public static void main( String args[] )
        throws SQLException , ClassNotFoundException {
        
        Class.forName( "com.mongodb.jdbc.MongoDriver" );
        
        Connection c = DriverManager.getConnection( "mongodb://10.241.14.41:12340/exampledb" );

        Statement stmt = c.createStatement();
        
        // drop old table
        stmt.executeUpdate( "drop table people" );

        // insert some data
        stmt.executeUpdate( "insert into people ( name , age ) values ( 'eliot' , 30 )" );
        stmt.executeUpdate( "insert into people ( name , age ) values ( 'sara' , 2 )" );
        stmt.executeUpdate( "insert into people ( name , age ) values ( 'jaime' , 28 )" );
        

        // print
        print( "not sorted" , stmt.executeQuery( "select addname , age from people " ) );
        print( "sorted by age" , stmt.executeQuery( "select name , age from people order by age " ) );
        print( "sorted by age desc" , stmt.executeQuery( "select name , age from people order by age desc limit 2,1" ) );

        // update
        stmt.executeUpdate( "update people set age=addtoset(32) where name='jaime'" );
        print( "sorted by age desc" , stmt.executeQuery( "select name , age from people order by age desc " ) );

    }

} 
