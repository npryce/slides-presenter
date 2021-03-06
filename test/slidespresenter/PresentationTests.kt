package slidespresenter

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import slidespresenter.Direction.next
import slidespresenter.Direction.previous

class PresentationTests {

    @Test fun `parse text as presentation`() {
        assertThat(emptyList<String>().parseAsPresentation(), equalTo<Presentation>(null))

        assertThat(listOf("slide1.png").parseAsPresentation(), equalTo(
            Presentation(slides = listOf("slide1.png"))
        ))

        assertThat(listOf("slide1.png", "", "  ", "-- comment", "# comment", "slide2.png").parseAsPresentation(), equalTo(
            Presentation(slides = listOf("slide1.png", "slide2.png"))
        ))

        assertThat(listOf("slide{{next 3}}.png", "some-slide.png", "slide{{next 3}}.png").parseAsPresentation(), equalTo(
            Presentation(slides = listOf(
                "slide001.png", "slide002.png", "slide003.png",
                "some-slide.png",
                "slide004.png", "slide005.png", "slide006.png"
            ))
        ))
    }

    @Test fun `move to next, previous slide`() {
        val it = Presentation(slides = listOf("slide1", "slide2", "slide3"))

        assertThat(it.currentSlide, equalTo(""))

        assertThat(it.moveSlide(previous).currentSlide, equalTo("slide1"))
        assertThat(it.moveSlide(next).currentSlide, equalTo("slide1"))
        assertThat(it.moveSlide(next).moveSlide(next).currentSlide, equalTo("slide2"))
        assertThat(it.moveSlide(next).moveSlide(previous).currentSlide, equalTo("slide1"))

        assertThat(it.moveSlide(next).moveSlide(next).moveSlide(next).currentSlide, equalTo("slide3"))
        assertThat(it.moveSlide(next, stepSize = 3).currentSlide, equalTo("slide3"))
        assertThat(it.moveSlide(next, stepSize = 4).currentSlide, equalTo("slide3"))
    }

    @Test fun `load state from another presentation`() {
        assertThat(
            Presentation(slides = listOf("slide1", "slide2", "slide3"))
                .loadStateFrom(Presentation(slides = listOf("slide1", "slide2"), currentSlide = "slide2"))
                .currentSlide,
            equalTo("slide2")
        )
        assertThat(
            Presentation(slides = listOf("slide1", "slide2", "slide3"))
                .loadStateFrom(Presentation(slides = listOf("slide0", "slide2"), currentSlide = "slide0"))
                .currentSlide,
            equalTo("")
        )
    }
}