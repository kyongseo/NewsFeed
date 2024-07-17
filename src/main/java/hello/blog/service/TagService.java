package hello.blog.service;

import hello.blog.domain.Post;
//import hello.blog.domain.Tag;
//import hello.blog.repository.PostRepository;
////import hello.blog.repository.TagRepository;
//import hello.blog.repository.TagRepository;
import hello.blog.domain.Tag;
import hello.blog.repository.PostRepository;
import hello.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public List<Post> findPostsByTag(String tagName) {
        Optional<Tag> tagOptional = tagRepository.findByName(tagName);

        if (tagOptional.isPresent()) {
            Tag tag = tagOptional.get();
            return postRepository.findByTagName(tag.getName());
        }

        return Collections.emptyList();
    }
}
