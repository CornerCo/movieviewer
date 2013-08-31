package com.github.cornerco.movieviewer;

/* An example of a very simple, multi-threaded HTTP server.
 * Implementation notes are in WebServer.html, and also
 * as comments in the source code.
 */
import java.io.*;
import java.net.*;
import java.util.*;

class WebServer implements HttpConstants {

    String green = "style=\"color:rgb(44,249,38)\"";
    String red = "style=\"color:rgb(255,145,145)\"";
    String cyan = "style=\"color:rgb(36,209,219)\"";
    String white = "style=\"color:rgb(255,255,255)\"";
    String orange = "style=\"color:rgb(242,190,13)\"";
    String black = "style=\"color:black\"";
    String colorS = "";
    String alphaSet = "";
    /* static class data/methods */

    /* print to stdout */
    protected static void p(String s) {
        System.out.println(s);
    }

    /* print to the log file */
    protected static void log(String s) {
        synchronized (log) {
            log.println(s);
            log.flush();
        }
    }
    static PrintStream log = null;
    /* our server's configuration information is stored
     * in these properties
     */
    protected static Properties props = new Properties();

    /* Where worker threads stand idle */
    static ArrayList threads = new ArrayList();

    /* the web server's virtual root */
    static File root;

    /* timeout on client connections */
    static int timeout = 0;

    /* max # worker threads */
    static int workers = 5;
    String parameter = "";
    String framepointer = "";
    static MovieViewer mv = null;
    boolean appSelectionActive = false;

    /* load www-server.properties from java.home */
    static void loadProps() throws IOException {
        File f = new File(System.getProperty("java.home") + File.separator
                + "lib" + File.separator + "www-server.properties");
        if (f.exists()) {
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            props.load(is);
            is.close();
            String r = props.getProperty("root");
            if (r != null) {
                root = new File(r);
                if (!root.exists()) {
                    throw new Error(root + " doesn't exist as server root");
                }
            }
            r = props.getProperty("timeout");
            if (r != null) {
                timeout = Integer.parseInt(r);
            }
            r = props.getProperty("workers");
            if (r != null) {
                workers = Integer.parseInt(r);
            }
            r = props.getProperty("log");
            if (r != null) {
                p("opening log file: " + r);
                log = new PrintStream(new BufferedOutputStream(
                        new FileOutputStream(r)));
            }
        }

        /* if no properties were specified, choose defaults */
        if (root == null) {
            root = new File(System.getProperty("user.dir"));
        }
        if (timeout <= 1000) {
            timeout = 200000;
        }
        if (workers < 25) {
            workers = 5;
        }
        if (log == null) {
            p("logging to stdout");
            log = System.out;
        }
    }

    static void printProps() {
        p("root=" + root);
        p("timeout=" + timeout);
        p("workers=" + workers);
    }

    public static void main(String[] a) throws Exception {
        int port = 80;
        if (a.length > 0) {
            port = Integer.parseInt(a[0]);
        }
        loadProps();
        printProps();

        mv = new MovieViewer();
        mv.setVisible(true);
        /* start worker threads */
        for (int i = 0; i < workers; ++i) {
            Worker w = new Worker();
            (new Thread(w, "worker #" + i)).start();
            threads.add(w);
        }

        ServerSocket ss = new ServerSocket(port);
        ss.setSoTimeout(0);
        ss.setPerformancePreferences(2, 1, 1);

        while (true) {

            Socket s = ss.accept();
            s.setSoTimeout(0);
            s.setTcpNoDelay(true);
            s.setKeepAlive(true);
            s.setPerformancePreferences(2, 1, 1);

            Worker w = null;
            synchronized (threads) {
                if (threads.isEmpty()) {
                    Worker ws = new Worker();
                    ws.setSocket(s);
                    (new Thread(ws, "additional worker")).start();
                } else {
                    w = (Worker) threads.get(0);
                    threads.remove(0);
                    w.setSocket(s);
                }
            }
        }
    }
}

class Worker extends WebServer implements HttpConstants, Runnable {

    final static int BUF_SIZE = 2048;
    static final byte[] EOL = {(byte) '\r', (byte) '\n'};

    /* buffer to use for requests */
    byte[] buf;
    /* Socket to client we're handling */
    private Socket s;

    Worker() {
        buf = new byte[BUF_SIZE];
        s = null;
    }

    synchronized void setSocket(Socket s) {
        this.s = s;
        notify();
    }

    public synchronized void run() {
        while (true) {
            if (s == null) {
                /* nothing to do */
                try {
                    wait();
                } catch (InterruptedException e) {
                    /* should not happen */
                    continue;
                }
            }
            try {
                handleClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /* go back in wait queue if there's fewer
             * than numHandler connections.
             */
            s = null;
            ArrayList pool = WebServer.threads;
            synchronized (pool) {
                if (pool.size() >= WebServer.workers) {
                    /* too many threads, exit this one */
                    return;
                } else {
                    pool.add(this);
                }
            }
        }
    }

    void handleClient() throws IOException {
        InputStream is = new BufferedInputStream(s.getInputStream());
        PrintStream ps = new PrintStream(s.getOutputStream());
        /* we will only block in read for this many milliseconds
         * before we fail with java.io.InterruptedIOException,
         * at which point we will abandon the connection.
         */

        // s.setSoTimeout(WebServer.timeout);


        /* zero out the buffer from last time */
        for (int i = 0; i < BUF_SIZE; i++) {
            buf[i] = 0;
        }
        try {
            /* We only support HTTP GET/HEAD, and don't
             * support any fancy HTTP options,
             * so we're only interested really in
             * the first line.
             */
            int nread = 0, r = 0;

            outerloop:
            while (nread < BUF_SIZE) {
                r = is.read(buf, nread, BUF_SIZE - nread);
                if (r == -1) {
                    /* EOF */
                    return;
                }
                int i = nread;
                nread += r;
                for (; i < nread; i++) {
                    if (buf[i] == (byte) '\n' || buf[i] == (byte) '\r') {
                        /* read one line */
                        break outerloop;
                    }
                }
            }

            /* are we doing a GET or just a HEAD */
            boolean doingGet;

            /* beginning of file name */
            int index;
            //System.out.println(buf[0]);

            if (buf[0] == (byte) 'G'
                    && buf[1] == (byte) 'E'
                    && buf[2] == (byte) 'T'
                    && buf[3] == (byte) ' ') {
                doingGet = true;
                index = 4;
            } else if (buf[0] == (byte) 'H'
                    && buf[1] == (byte) 'E'
                    && buf[2] == (byte) 'A'
                    && buf[3] == (byte) 'D'
                    && buf[4] == (byte) ' ') {
                doingGet = false;
                index = 5;

            } else {
                /* we don't support this method */
                ps.print("HTTP/1.0 " + HTTP_BAD_METHOD
                        + " unsupported method type: ");
                ps.write(buf, 0, 5);
                ps.write(EOL);
                ps.flush();
                s.close();
                return;
            }


            int i = 0;
            /* find the file name, from:
             * GET /foo/bar.html HTTP/1.0
             * extract "/foo/bar.html"
             */
            for (i = index; i < nread; i++) {
                if (buf[i] == (byte) ' ') {
                    break;
                }
            }
            String fname = (new String(buf, 0, index,
                    i - index)).replace('/', File.separatorChar);
            if (fname.startsWith(File.separator)) {
                fname = fname.substring(1);
            }


            boolean special = fname.contains("?");
            parameter = "";
            framepointer = "";

            if (special) {
                // System.out.println(">>>>>>>>>>special");
                int x = fname.lastIndexOf("\\");
                framepointer = fname.substring(x + 1, fname.indexOf("?"));
                // System.out.println(".........."+framepointer);
                parameter = fname.substring(fname.indexOf("?") + 1, fname.length());
                // System.out.println(".........."+parameter);

            }
            File targ = null;
            if (!special) {
                targ = new File(WebServer.root, fname);
                // System.out.println(">>>>>>>>>>>>"+targ.getAbsolutePath());
                if (targ.isDirectory()) {

                    File ind = new File(targ, "index.html");
                    if (ind.exists()) {
                        targ = ind;
                    }
                }
                boolean OK = printHeaders(targ, ps);
                if (doingGet) {
                    if (OK) {
                        sendFile(targ, ps);
                    } else {
                        send404(targ, ps);
                    }
                }
            } else {

                printSpecialHeaders(ps);
                sendSpecial(framepointer, ps);


            }



        } finally {
            s.close();
        }
    }

    boolean printHeaders(File targ, PrintStream ps) throws IOException {
        boolean ret = false;
        int rCode = 0;
        if (!targ.exists()) {
            rCode = HTTP_NOT_FOUND;
            ps.print("HTTP/1.0 " + HTTP_NOT_FOUND + " not found");
            ps.write(EOL);
            ret = false;
        } else {
            rCode = HTTP_OK;
            ps.print("HTTP/1.0 " + HTTP_OK + " OK");
            ps.write(EOL);
            ret = true;
        }
        // log("From " +s.getInetAddress().getHostAddress()+": GET " +
        //     targ.getAbsolutePath()+"-->"+rCode);
        ps.print("Server: Simple java");
        ps.write(EOL);
        ps.print("Date: " + (new Date()));
        ps.write(EOL);
        if (ret) {
            if (!targ.isDirectory()) {
                ps.print("Content-length: " + targ.length());
                ps.write(EOL);
                ps.print("Last Modified: " + (new Date(targ.lastModified())));
                ps.write(EOL);
                String name = targ.getName();
                int ind = name.lastIndexOf('.');
                String ct = null;
                if (ind > 0) {
                    ct = (String) map.get(name.substring(ind));
                }
                if (ct == null) {
                    ct = "unknown/unknown";
                }
                ps.print("Content-type: " + ct);
                ps.write(EOL);
            } else {
                ps.print("Content-type: text/html");
                ps.write(EOL);
            }
        }
        return ret;
    }

    void send404(File targ, PrintStream ps) throws IOException {
        ps.write(EOL);
        ps.write(EOL);
        ps.println("Not Found\n\n"
                + "The requested resource was not found.\n");
    }

    void sendFile(File targ, PrintStream ps) throws IOException {
        InputStream is = null;
        ps.write(EOL);
        //System.out.println(">>>>>>>>>>>>"+targ);

        if (targ.isDirectory()) {
            listDirectory(targ, ps);
            return;
        } else {
            is = new FileInputStream(targ.getAbsolutePath());
        }

        try {
            int n;
            while ((n = is.read(buf)) > 0) {
                ps.write(buf, 0, n);
            }
        } finally {
            is.close();
        }
    }

    boolean printSpecialHeaders(PrintStream ps) throws IOException {
        boolean ret = false;
        int rCode = 0;
        rCode = HTTP_OK;
        ps.print("HTTP/1.0 " + HTTP_OK + " OK");
        ps.write(EOL);
        ret = true;

        ps.print("Server: Simple java");
        ps.write(EOL);
        ps.print("Date: " + (new Date()));
        ps.write(EOL);

        ps.print("Content-type: text/html");
        ps.write(EOL);

        return ret;
    }

    void sendSpecial(String filename, PrintStream ps) throws IOException {
        InputStream is = null;
        //System.out.println(appSelectionActive);  
        ps.write(EOL);
        boolean prot = false;
        //System.out.println(">>>>>>>>>>>>"+targ);

        // ps.println("<title>Servlet NewServlet</title>");  

        if (parameter.startsWith("Nav.x")) {
            int index = parameter.indexOf("Nav=");
            if (index != -1) {
                parameter = parameter.substring(index);
            }

        }

        if (filename.equals("Cat")) {
            ps.println("<html>");
            ps.println("<head>");
            ps.println("</head>");

            // System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>"); 
            if (!parameter.equals("xxx")) {
                ps.println("<body style=\"background-color:rgb(0,43,85)\">");
                parameter = parameter.replace("%20", " ");
                try {
                    mv.setCategory(parameter);
                } catch (Exception e) {
                }

                this.getMovies(ps);

            } else {

                ps.println("<body style=\"background-color:rgb(227,254,224)\">");
                String cat[] = mv.getCategorisations();
                ps.println("<table>");

                for (int c = 0; c < cat.length; c++) {
                    // colorS = black;
                    // if(c==mv.getSelectedCat()) 
                    //         colorS=red;

                    ps.println("<tr>");
                    ps.println("<td>");
                    ps.println("<a href=\"Cat?" + cat[c] + "\" " + black + " target=\"movies\">" + cat[c] + "</a>");
                    ps.println("</td>");
                    ps.println("</tr>");
                }

                ps.println("</table>");


                ps.println("</body>");
                ps.println("</html>");
            }

        } else if (filename.equals("Nav")) {
            if (!parameter.equals("=xxx")) {
                // String extraParameter = parameter.substring(parameter.indexOf("&"),parameter.length()-1);
                // System.out.println(extraParameter);

                parameter = parameter.replace("Nav=", "");

                //parameter = parameter.replace(extraParameter, "");
                //System.out.println(">>>>>>>>>>>>>parameter>>>>>>>>>>>>" +parameter); 

                if (parameter.equals("next")) {
                    mv.nextChapter();
                } else if (parameter.equals("prev")) {
                    mv.pevChapter();
                } else if (parameter.equals("++play++")) {

                    prot = mv.checkProtected(mv.getMoviePath());
                    if (!prot) {
                        mv.play();
                    }

                } else if (parameter.equals("pause")) {
                    mv.pause();
                } else if (parameter.equals("up")) {

                    mv.menuUp();
                } else if (parameter.equals("down")) {
                    mv.menuDown();
                } else if (parameter.equals("i")) {
                    mv.info();
                } else if (parameter.equals("alpha") || parameter.equals("view")) {
                    mv.toggleAlpha();

                } else if (parameter.equals("5back")) {
                    mv.jumpB();
                } else if (parameter.equals("5forw")) {
                    mv.jumpF();
                } else if (parameter.equals("close")) {
                    /* if(appSelectionActive)
                     {
                     mv.closeAppSelect();
                        
                     appSelectionActive = false;
                     }
                     else
                     {*/

                    mv.closeApp();
                    // }
                } /* else if (parameter.equals("selectApp"))
                 {
                 appSelectionActive = true; 
                 //System.out.println("<<<<<<"+appSelectionActive); 
                 mv.appSelect();
                      
                 }*/ else if (parameter.startsWith("Text=")) {
                    parameter = parameter.replace("Text=", "");
                    if (parameter.equals("")) {
                        parameter = "0";
                    }
                    try {
                        int position = Integer.parseInt(parameter);
                        mv.moveToPosition(position);
                    } catch (Exception e) {
                    }

                } else if (parameter.startsWith("Pass=")) {
                    //System.out.println("got "+ parameter);
                    parameter = parameter.replace("Pass=", "");
                    System.out.println("dasfdsa");
                    System.out.println(parameter);
                    if (parameter.equals(mv.getPassword())) {
                        mv.play();

                    }
                }
            }

            ps.println("<html>");
            ps.println("<head>");
            ps.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

            ps.println("</head>");


            ps.println("<body style=\"background-color:rgb(254,253,224)\">");
            if (!prot) {
                ps.println("<form action=\"./Nav\" method=GET>");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"  play  \">");
                ///////////ps.println("<INPUT type=\"image\" src=\"play-start.gif\" name=\"Nav\" value=\"  play  \">");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"pause\">");
                //////////// ps.println("<INPUT type=\"image\" src=\"play-pause.gif\" name=\"Nav\" value=\"pause\">");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"next\">");
                ///////////// ps.println("<INPUT type=\"image\" src=\"play-fb.gif\" name=\"Nav\" value=\"next\">"); 
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"prev\">");
                ////////// ps.println("<INPUT type=\"image\" src=\"play-ff.gif\" name=\"Nav\" value=\"prev\">");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"close\">");



                ps.println("</form>");

                ps.println("<table><tr><td>");

                ps.println("<form action=\"./Nav\" method=GET>");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"5back\">");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"5forw\">");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"up\">");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"down\">");
                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"i\">");

                alphaSet = "view";
                if (mv.isAlphaSet()) {
                    alphaSet = "alpha";
                }

                ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"" + alphaSet + "\">");
                ps.println("</form>");
                ps.println("</td>");

                ps.println("<td>");
                ps.println("<form action=\"./Nav\" method=GET>");
                ps.println("<INPUT type=\"text\" size=\"7\" name=\"Text\" value=\"\">");
                ps.println("</form>");
                ps.println("</td>");
                ps.println("</tr> </table>");

                //ps.println("<form action=\"./Nav\" method=GET>");
                //ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"up\">");
                // ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"down\">");
                // ps.println("<INPUT type=\"submit\" name=\"Nav\" value=\"i\">");
                // ps.println("</form>");
            } else {
                ps.println("</form>");
                ps.println("<form action=\"./Nav\" method=GET>");
                ps.println("<INPUT type=\"text\" name=\"Pass\" value=\"\">");
                ps.println("</form>");

            }

            ps.println("</body>");
            ps.println("</html>");

        } else if (filename.equals("View")) {
            if (!parameter.equals("xxx")) {
                mv.selectMovie(Integer.parseInt(parameter));

            } else {
                //show default movie list on startup
                ps.println("<html>");
                ps.println("<head>");
                ps.println("</head>");
                ps.println("<body style=\"background-color:rgb(0,43,85)\">");
                getMovies(ps);
                ps.println("</body>");
                ps.println("</html>");
            }

        }
    }

    /* mapping of file extensions to content-types */
    static java.util.HashMap map = new java.util.HashMap();

    static {
        fillMap();
    }

    static void setSuffix(String k, String v) {
        map.put(k, v);
    }

    private void getMovies(PrintStream ps) {
        ArrayList movies = mv.getMovieList();

        for (int c = 0; c < movies.size(); c++) {
            MovieEntry me = (MovieEntry) movies.get(c);
            int viewings = me.getViewings();
            if (viewings == 0) {
                colorS = red;
            } else if (viewings == 1) {
                colorS = white;
            } else if (viewings <= 3) {
                colorS = green;
            } else if (viewings <= 5) {
                colorS = cyan;
            } else {
                colorS = orange;
            }

            ps.println("<table>");
            ps.println("<tr>");
            ps.println("<td>");
            //ps.println("<a href=\"View?"+c+"\" style=\"color:rgb(255,255,0)\" target=\"hiddenFrame\">"+me.getTitle()+"</a>");
            ps.println("<a href=\"View?" + c + "\" " + colorS + "target=\"hiddenFrame\">" + me.getTitle() + "</a>");

            ps.println("</td>");
            ps.println("</tr>");

        }
        ps.println("</table>");
    }

    static void fillMap() {
        setSuffix("", "content/unknown");
        setSuffix(".uu", "application/octet-stream");
        setSuffix(".exe", "application/octet-stream");
        setSuffix(".ps", "application/postscript");
        setSuffix(".zip", "application/zip");
        setSuffix(".sh", "application/x-shar");
        setSuffix(".tar", "application/x-tar");
        setSuffix(".snd", "audio/basic");
        setSuffix(".au", "audio/basic");
        setSuffix(".wav", "audio/x-wav");
        setSuffix(".gif", "image/gif");
        setSuffix(".jpg", "image/jpeg");
        setSuffix(".jpeg", "image/jpeg");
        setSuffix(".htm", "text/html");
        setSuffix(".html", "text/html");
        setSuffix(".text", "text/plain");
        setSuffix(".c", "text/plain");
        setSuffix(".cc", "text/plain");
        setSuffix(".c++", "text/plain");
        setSuffix(".h", "text/plain");
        setSuffix(".pl", "text/plain");
        setSuffix(".txt", "text/plain");
        setSuffix(".java", "text/plain");
    }

    void listDirectory(File dir, PrintStream ps) throws IOException {
        ps.println("<TITLE>Directory listing</TITLE><P>\n");
        ps.println("<A HREF=\"..\">Parent Directory</A><BR>\n");
        String[] list = dir.list();
        for (int i = 0; list != null && i < list.length; i++) {
            File f = new File(dir, list[i]);
            if (f.isDirectory()) {
                ps.println("<A HREF=\"" + list[i] + "/\">" + list[i] + "/</A><BR>");
            } else {
                ps.println("<A HREF=\"" + list[i] + "\">" + list[i] + "</A><BR");
            }
        }
        ps.println("<P><HR><BR><I>" + (new Date()) + "</I>");
    }
}

interface HttpConstants {

    /**
     * 2XX: generally "OK"
     */
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_ACCEPTED = 202;
    public static final int HTTP_NOT_AUTHORITATIVE = 203;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_RESET = 205;
    public static final int HTTP_PARTIAL = 206;
    /**
     * 3XX: relocation/redirect
     */
    public static final int HTTP_MULT_CHOICE = 300;
    public static final int HTTP_MOVED_PERM = 301;
    public static final int HTTP_MOVED_TEMP = 302;
    public static final int HTTP_SEE_OTHER = 303;
    public static final int HTTP_NOT_MODIFIED = 304;
    public static final int HTTP_USE_PROXY = 305;
    /**
     * 4XX: client error
     */
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_PAYMENT_REQUIRED = 402;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_BAD_METHOD = 405;
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    public static final int HTTP_PROXY_AUTH = 407;
    public static final int HTTP_CLIENT_TIMEOUT = 408;
    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_GONE = 410;
    public static final int HTTP_LENGTH_REQUIRED = 411;
    public static final int HTTP_PRECON_FAILED = 412;
    public static final int HTTP_ENTITY_TOO_LARGE = 413;
    public static final int HTTP_REQ_TOO_LONG = 414;
    public static final int HTTP_UNSUPPORTED_TYPE = 415;
    /**
     * 5XX: server error
     */
    public static final int HTTP_SERVER_ERROR = 500;
    public static final int HTTP_INTERNAL_ERROR = 501;
    public static final int HTTP_BAD_GATEWAY = 502;
    public static final int HTTP_UNAVAILABLE = 503;
    public static final int HTTP_GATEWAY_TIMEOUT = 504;
    public static final int HTTP_VERSION = 505;
}
