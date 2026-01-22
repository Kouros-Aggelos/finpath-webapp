// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.finpath.controller;

import com.finpath.util.DbConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AppController extends HttpServlet {
   private static final String JSP_WELCOME = "/welcome.jsp";
   private static final String JSP_LOGIN = "/login.jsp";
   private static final String JSP_REGISTER = "/register.jsp";
   private static final String JSP_QUESTIONNAIRE = "/Erwtimatologio.jsp";

   public AppController() {
   }

   protected void doGet(HttpServletRequest var1, HttpServletResponse var2) throws ServletException, IOException {
      String var3 = var1.getServletPath();
      HttpSession var4 = var1.getSession(false);
      switch (var3) {
         case "/welcome":
            var1.getRequestDispatcher("/welcome.jsp").forward(var1, var2);
            break;
         case "/login":
            var1.getRequestDispatcher("/login.jsp").forward(var1, var2);
            break;
         case "/register":
            var1.getRequestDispatcher("/register.jsp").forward(var1, var2);
            break;
         case "/questionnaire":
            if (var4 != null && var4.getAttribute("userId") != null) {
               var1.getRequestDispatcher("/Erwtimatologio.jsp").forward(var1, var2);
            } else {
               var2.sendRedirect(var1.getContextPath() + "/login");
            }
            break;
         case "/logout":
            if (var4 != null) {
               var4.invalidate();
            }

            var2.sendRedirect(var1.getContextPath() + "/welcome");
            break;
         default:
            var2.sendError(404);
      }

   }

   protected void doPost(HttpServletRequest var1, HttpServletResponse var2) throws ServletException, IOException {
      String var3 = var1.getServletPath();
      if ("/login".equals(var3)) {
         this.handleLogin(var1, var2);
      } else if ("/register".equals(var3)) {
         this.handleRegister(var1, var2);
      } else {
         this.doGet(var1, var2);
      }

   }

   private void handleLogin(HttpServletRequest var1, HttpServletResponse var2) throws IOException, ServletException {
      String var3 = var1.getParameter("username");
      String var4 = var1.getParameter("password");
      if (!this.empty(var3) && !this.empty(var4)) {
         try {
            Connection var5 = DbConnection.getConnection();

            try {
               PreparedStatement var6 = var5.prepareStatement("SELECT user_id FROM users WHERE username=? AND password=?");

               try {
                  var6.setString(1, var3);
                  var6.setString(2, var4);
                  ResultSet var7 = var6.executeQuery();

                  try {
                     if (var7.next()) {
                        int var8 = var7.getInt(1);
                        var1.getSession(true).setAttribute("userId", var8);
                        var2.sendRedirect(var1.getContextPath() + "/questionnaire");
                     } else {
                        var1.setAttribute("error", "Λάθος στοιχεία.");
                        var1.getRequestDispatcher("/login.jsp").forward(var1, var2);
                     }
                  } catch (Throwable var13) {
                     if (var7 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var12) {
                           var13.addSuppressed(var12);
                        }
                     }

                     throw var13;
                  }

                  if (var7 != null) {
                     var7.close();
                  }
               } catch (Throwable var14) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var11) {
                        var14.addSuppressed(var11);
                     }
                  }

                  throw var14;
               }

               if (var6 != null) {
                  var6.close();
               }
            } catch (Throwable var15) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var10) {
                     var15.addSuppressed(var10);
                  }
               }

               throw var15;
            }

            if (var5 != null) {
               var5.close();
            }

         } catch (SQLException var16) {
            throw new ServletException("DB error on login", var16);
         }
      } else {
         var1.setAttribute("error", "Συμπλήρωσε username και password.");
         var1.getRequestDispatcher("/login.jsp").forward(var1, var2);
      }
   }

   private void handleRegister(HttpServletRequest var1, HttpServletResponse var2) throws IOException, ServletException {
      String var3 = var1.getParameter("username");
      String var4 = var1.getParameter("password");
      String var5 = var1.getParameter("confirm");
      if (!this.empty(var3) && !this.empty(var4) && !this.empty(var5)) {
         if (!var4.equals(var5)) {
            var1.setAttribute("error", "Τα passwords δεν ταιριάζουν.");
            var1.getRequestDispatcher("/register.jsp").forward(var1, var2);
         } else {
            try {
               Connection var6 = DbConnection.getConnection();

               label134: {
                  try {
                     PreparedStatement var7 = var6.prepareStatement("SELECT user_id FROM users WHERE username=?");

                     label136: {
                        try {
                           var7.setString(1, var3);
                           if (!var7.executeQuery().next()) {
                              break label136;
                           }

                           var1.setAttribute("error", "Το username υπάρχει ήδη.");
                           var1.getRequestDispatcher("/register.jsp").forward(var1, var2);
                        } catch (Throwable var15) {
                           if (var7 != null) {
                              try {
                                 var7.close();
                              } catch (Throwable var12) {
                                 var15.addSuppressed(var12);
                              }
                           }

                           throw var15;
                        }

                        if (var7 != null) {
                           var7.close();
                        }
                        break label134;
                     }

                     if (var7 != null) {
                        var7.close();
                     }

                     var7 = var6.prepareStatement("INSERT INTO users(username, password) VALUES(?,?)", 1);

                     try {
                        var7.setString(1, var3);
                        var7.setString(2, var4);
                        var7.executeUpdate();
                        ResultSet var8 = var7.getGeneratedKeys();

                        try {
                           if (var8.next()) {
                              var1.getSession(true).setAttribute("userId", var8.getInt(1));
                           }
                        } catch (Throwable var16) {
                           if (var8 != null) {
                              try {
                                 var8.close();
                              } catch (Throwable var14) {
                                 var16.addSuppressed(var14);
                              }
                           }

                           throw var16;
                        }

                        if (var8 != null) {
                           var8.close();
                        }
                     } catch (Throwable var17) {
                        if (var7 != null) {
                           try {
                              var7.close();
                           } catch (Throwable var13) {
                              var17.addSuppressed(var13);
                           }
                        }

                        throw var17;
                     }

                     if (var7 != null) {
                        var7.close();
                     }

                     var2.sendRedirect(var1.getContextPath() + "/questionnaire");
                  } catch (Throwable var18) {
                     if (var6 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var11) {
                           var18.addSuppressed(var11);
                        }
                     }

                     throw var18;
                  }

                  if (var6 != null) {
                     var6.close();
                  }

                  return;
               }

               if (var6 != null) {
                  var6.close();
               }

            } catch (SQLException var19) {
               throw new ServletException("DB error on register", var19);
            }
         }
      } else {
         var1.setAttribute("error", "Συμπλήρωσε όλα τα πεδία.");
         var1.getRequestDispatcher("/register.jsp").forward(var1, var2);
      }
   }

   private boolean empty(String var1) {
      return var1 == null || var1.trim().isEmpty();
   }
}
