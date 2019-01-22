/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.superbiz.moviefun.albumapi;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @version $Revision$ $Date$
 */
@Component
public class AlbumServlet extends HttpServlet {

    private static final long serialVersionUID = -5832176047021911038L;

    public static int PAGE_SIZE = 5;

    private AlbumsClient albumsClient;

    public AlbumServlet(AlbumsClient albumsClient) {
        this.albumsClient = albumsClient;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("Add".equals(action)) {

            String title = request.getParameter("title");
            String artist = request.getParameter("artist");
            int rating = Integer.parseInt(request.getParameter("rating"));
            int year = Integer.parseInt(request.getParameter("year"));

            Album album = new Album(title, artist, rating, year);

            albumsClient.addAlbum(album);
            response.sendRedirect("albums");
            return;

        } else if ("Remove".equals(action)) {

            String[] ids = request.getParameterValues("id");
            for (String id : ids) {
                albumsClient.deleteAlbumId(new Long(id));
            }

            response.sendRedirect("albums");
            return;

        } else {
            String key = request.getParameter("key");
            String field = request.getParameter("field");

            int count = 0;

            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
                count = albumsClient.countAll();
                key = "";
                field = "";
            } else {
                count = albumsClient.count(field, key);
            }

            int page = 1;

            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (Exception e) {
            }

            int pageCount = (count / PAGE_SIZE);
            if (pageCount == 0 || count % PAGE_SIZE != 0) {
                pageCount++;
            }

            if (page < 1) {
                page = 1;
            }

            if (page > pageCount) {
                page = pageCount;
            }

            int start = (page - 1) * PAGE_SIZE;
            List<Album> range;

            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
                range = albumsClient.findAll(start, PAGE_SIZE);
            } else {
                range = albumsClient.findRange(field, key, start, PAGE_SIZE);
            }

            int end = start + range.size();

            request.setAttribute("count", count);
            request.setAttribute("start", start + 1);
            request.setAttribute("end", end);
            request.setAttribute("page", page);
            request.setAttribute("pageCount", pageCount);
            request.setAttribute("albums", range);
            request.setAttribute("key", key);
            request.setAttribute("field", field);
        }

        request.getRequestDispatcher("WEB-INF/albums.jsp").forward(request, response);
    }

}