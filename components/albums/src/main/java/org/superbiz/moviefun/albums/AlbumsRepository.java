package org.superbiz.moviefun.albums; /**
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

import org.apache.tika.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Repository
public class AlbumsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final BlobStore blobStore;

    public AlbumsRepository(BlobStore blobStore) {
        this.blobStore = blobStore;
    }

    @Transactional
    public void addAlbum(Album album) {
        entityManager.persist(album);
    }

    public Album find(long id) {
        return entityManager.find(Album.class, id);
    }

    public List<Album> getAlbums() {
        CriteriaQuery<Album> cq = entityManager.getCriteriaBuilder().createQuery(Album.class);
        cq.select(cq.from(Album.class));
        return entityManager.createQuery(cq).getResultList();
    }

    @Transactional
    public void deleteAlbum(Album album) {
        entityManager.remove(album);
    }

    @Transactional
    public void updateAlbum(Album album) {
        entityManager.merge(album);
    }

    public Map<String, byte[]> getCover(long albumId) {
        String coverName = this.getCoverBlobName(albumId);

        Optional<Blob> maybeCoverBlob = null;
        try {
            maybeCoverBlob = blobStore.get(coverName);
            Blob coverBlob = maybeCoverBlob.orElseGet(this::buildDefaultCoverBlob);
            byte[] imageBytes = IOUtils.toByteArray(coverBlob.inputStream);
            String contentType = coverBlob.contentType;
            Map<String, byte[]> myMap = new HashMap<String, byte[]>();
            myMap.putIfAbsent(contentType,imageBytes);
            return myMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void tryToUploadCover(@PathVariable Long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        Blob coverBlob = new Blob(
                getCoverBlobName(albumId),
                uploadedFile.getInputStream(),
                uploadedFile.getContentType()
        );

        blobStore.put(coverBlob);
    }

    public Blob buildDefaultCoverBlob() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream input = classLoader.getResourceAsStream("default-cover.jpg");

        return new Blob("default-cover", input, MediaType.IMAGE_JPEG_VALUE);
    }

    public String getCoverBlobName(@PathVariable long albumId) {
        return format("covers/%d", albumId);
    }
}
