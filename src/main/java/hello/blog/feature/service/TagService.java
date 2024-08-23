package hello.blog.feature.service;

import hello.blog.feature.domain.Post;
import hello.blog.feature.domain.Tag;
import hello.blog.feature.repository.PostRepository;
import hello.blog.feature.repository.TagRepository;
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
