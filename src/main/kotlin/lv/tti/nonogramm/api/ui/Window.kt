package lv.tti.nonogramm.api.ui

import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*
import com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT
import com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.awt.GLJPanel
import com.jogamp.opengl.fixedfunc.GLMatrixFunc
import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.util.gl2.GLUT
import lv.tti.nonogramm.api.stats.StatCollector
import javax.swing.JFrame
import jogamp.opengl.GLWorkerThread.isStarted
import com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.text.html.HTML.Attribute.TITLE


class Window(private val title: String, private val width: Int = 1024, private val height: Int = 768) {

	init {
		val canvas = GLJPanel()
		val frame = JFrame()
		frame.contentPane.add(canvas)
		canvas.addGLEventListener(Renderer())

		canvas.preferredSize = Dimension(width, height)

		val animator = FPSAnimator(canvas, 60, true)

		frame.addWindowListener(object : WindowAdapter() {
			override fun windowClosing(e: WindowEvent) {
				object : Thread() {
					override fun run() {
						if (animator.isStarted)
							animator.stop()
						System.exit(0)
					}
				}.start()
			}
		})

		frame.title = title;
		frame.pack()
		frame.isVisible = true;
		animator.start()
		while(true) {}

	}
}

class Renderer : GLEventListener {

	val glut = GLUT()

	override fun reshape(drawable: GLAutoDrawable?, x: Int, y: Int, width: Int, height: Int) {
		val gl: GL2ES2 = drawable!!.gl.gL2ES2

		gl.glViewport(0, 0, width, height)
	}

	override fun display(drawable: GLAutoDrawable?) {
		val gl = drawable!!.gl.gL2

		gl.glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

		val scores = StatCollector.scores
		val averages = StatCollector.averageScores

		ortho(gl, -scores.size.toDouble() / 10, scores.size.toDouble() + scores.size.toDouble() / 10, -6.0, 2.0)

		gl.glRasterPos2f(-scores.size / 10f, 1.5f)
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, "Max fitness: " + StatCollector.max)

		gl.glRasterPos2f(-scores.size / 10f, 1.25f)
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, "Average fitness: " + StatCollector.average)
		gl.glRasterPos2f(-scores.size / 10f, 1f)
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, "Iteration fitness: " + StatCollector.iteration)

		gl.glColor3f(0.0f, 1.0f, 1.0f)
		gl.glLineWidth(2f)
		gl.glBegin(GL2.GL_LINES)

		gl.glVertex2d(0.0, 0.0)
		gl.glVertex2d(0.0, 1.0)
		gl.glVertex2d(0.0, 0.0)
		gl.glVertex2d(scores.size.toDouble(), 0.0)

		gl.glEnd()

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (i in 0 until scores.size) {
			gl.glVertex2d(i.toDouble(), scores[i])
		}
		gl.glEnd()

		gl.glColor3f(0.0f, 1.0f, 0.0f)

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (i in 0 until averages.size) {
			gl.glVertex2d(i.toDouble(), averages[i])
		}
		gl.glEnd()

		val matrix = StatCollector.matrix!!

		ortho(gl,matrix[0].size + matrix[0].size/20.0, -matrix[0].size/20.0, matrix.size + matrix.size / 10.0, -matrix.size/2.0)

		for (i in 0 until matrix.size) {
			for (j in 0 until matrix[0].size) {
				gl.glColor3f(1.0f, 1.0f, 1.0f)
				gl.glLineWidth(2f)
				if (matrix[i][j]) gl.glBegin(GL2.GL_LINE_LOOP)
				else gl.glBegin(GL2.GL_POLYGON)


				gl.glVertex2i(matrix[0].size - j, i)
				gl.glVertex2i(matrix[0].size - j, i + 1)
				gl.glVertex2i(matrix[0].size - j - 1, i + 1)
				gl.glVertex2i(matrix[0].size - j - 1, i)
				gl.glEnd()
			}
		}
		drawable.swapBuffers()
	}

	override fun dispose(drawable: GLAutoDrawable?) {
	}

	override fun init(drawable: GLAutoDrawable) {
	}

	private fun ortho(gl: GL2, x1: Double, x2: Double, y1: Double, y2: Double) {
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION)
		gl.glLoadIdentity()
		gl.glOrtho(x1, x2, y1, y2, 1.0, -1.0);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}