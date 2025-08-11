package com.recommender.paper_recommender.service;

import com.recommender.paper_recommender.model.Paper;
import com.recommender.paper_recommender.model.SavedPaper;
import com.recommender.paper_recommender.model.User;
import com.recommender.paper_recommender.repository.PaperRepository;
import com.recommender.paper_recommender.repository.SavedPaperRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// This tells JUnit 5 to enable Mockito for this test class
@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    // @Mock creates a "fake" version of a dependency. We can control its behavior.
    @Mock
    private SavedPaperRepository savedPaperRepository;
    @Mock
    private PaperRepository paperRepository;
    @Mock
    private UserService userService;

    // @InjectMocks creates a real instance of BookmarkService and automatically
    // injects the @Mock objects created above into it.
    @InjectMocks
    private BookmarkService bookmarkService;

    // The @Test annotation marks this as a test method.
    @Test
    void whenSavingAnAlreadySavedPaper_thenThrowException() {
        // --- ARRANGE ---
        // We set up the conditions for our test.

        // 1. Define the test data
        String userEmail = "test@example.com";
        String paperId = "12345";
        User mockUser = new User();
        mockUser.setId(1L); // Set an ID for the mock user
        Paper mockPaper = new Paper();

        // 2. Define the behavior of our mocks.
        // When userService.findUserByEmail is called with our test email, return our mockUser.
        when(userService.findUserByEmail(userEmail)).thenReturn(mockUser);
        // When paperRepository.findById is called, return our mockPaper.
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(mockPaper));
        // THIS IS THE KEY: When the repository is asked if this paper is already saved,
        // we tell it to return a non-empty Optional, simulating that it *is* already saved.
        when(savedPaperRepository.findByUserIdAndPaperPaperId(1L, paperId))
                .thenReturn(Optional.of(new SavedPaper(mockUser, mockPaper)));


        // --- ACT & ASSERT ---
        // We perform the action and check if the result is what we expect.

        // We assert that calling the savePaper method under these conditions
        // MUST throw an IllegalStateException.
        assertThrows(IllegalStateException.class, () -> {
            bookmarkService.savePaper(userEmail, paperId);
        });
    }
}