/**
 * The Simple Library Managment System is a simple application, which uses MySql and Java Swing technologies.
 * It has been created for learning purposes.
 * It has two modes:
 *  1) admin: you can add, remove or edit books in database
 *  2) user: you can check (borrow) book from library or return to library
 *
 *  Run Start.java for start app.
 *
 * @author buarzi
 * @version 1.0
 * @since 2019-04-01
 */

package pl.buarzi;

import pl.buarzi.gui.StartWindow;

public class Start {
    public static void main(String[] args) {
        new StartWindow();
    }
}
