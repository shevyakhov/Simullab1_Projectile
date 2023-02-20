package com.chelz.simullab1_projectile

import android.graphics.Color
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chelz.simullab1_projectile.databinding.ActivityMainBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.lang.Math.cos
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Random
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

	val df = DecimalFormat("#.##")
	val C = 0.15
	val rho = 1.29
	val g = 9.81

	var dt = 0.0
	val sets = ArrayList<LineDataSet>()

	var x: Double = 0.0
	var y: Double = 0.0
	var y0: Double = 0.0
	var v0: Double = 0.0
	var a: Double = 0.0
	var t: Double = 0.0
	var sina: Double = 0.0
	var cosa: Double = 0.0
	var m: Double = 0.0
	var s: Double = 0.0
	var k: Double = 0.0
	var vx: Double = 0.0
	var vy: Double = 0.0
	var maxy = 0.0
	lateinit var binding: ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		df.roundingMode = RoundingMode.CEILING
		binding.addGraph.setOnClickListener {
			addGraph()
		}

	}

	private fun addGraph() {
		dt = binding.edStep.text.toString().toDouble()
		val angle = binding.edAngle.text.toString().toDouble()
		a = if (angle > 90) angle else 180 - angle
		m = binding.edWeight.text.toString().toDouble()
		y0 = binding.edHeight.text.toString().toDouble()
		s = binding.edSize.text.toString().toDouble()
		v0 = binding.edSpeed.text.toString().toDouble()
		sina = sin(a * Math.PI / 180)
		cosa = cos(a * Math.PI / 180)
		vx = v0 * cosa
		vy = v0 * sina
		k = 0.5 * C * s * rho / m
		t = 0.0
		x = 0.0
		maxy - 0.0
		y = y0

		val lineEntry = ArrayList<Entry>()
		lineEntry.add(Entry(0f, y0.toFloat()))

		do {
			timerTick(lineEntry)
		} while (y > 0)

		val lineDataset = LineDataSet(lineEntry, "${sets.size}")
		val rnd = Random()
		val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
		lineDataset.color = color
		lineDataset.setCircleColor(color)
		lineDataset.circleRadius = 1f
		lineDataset.lineWidth = 3F
		sets.add(lineDataset)
		anim()

		var xAxis: XAxis
		run {
			xAxis = binding.chart.xAxis
			xAxis.mAxisMinimum = 0f
			xAxis.enableGridDashedLine(10f, 10f, 0f)
		}

	}

	private fun timerTick(lineEntry: ArrayList<Entry>) {

		try {
			t += dt
			val v = sqrt(vx * vx + vy * vy)
			vx -= k * vx * v * dt
			vy -= (g + k * vy * v) * dt

			x += vx * dt
			y += vy * dt
			if (y > maxy) maxy = y
			if (y >= 0) {
				val entry = Entry(x.toFloat(), y.toFloat())
				lineEntry.add(entry)
			} else {
				val entry = Entry(x.toFloat(), 0.toFloat())
				lineEntry.add(entry)
				addRow(sets.size, dt, x, maxy, sqrt(vx * vx + vy * vy))
			}
		} catch (e: java.lang.Exception) {
			Toast.makeText(this, "Не хватает памяти", Toast.LENGTH_SHORT).show()
		}

	}

	private fun anim() {
		val data = LineData(sets as List<ILineDataSet>?)
		binding.chart.data = data
		binding.chart.animateXY(1000, 1000)

	}

	private fun addRow(_num: Int, _step: Double, _distance: Double, _maxh: Double, _endSpeed: Double) {

		val tl = binding.table
		val tr = TableRow(this)
		tr.layoutParams = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)

		val num = TextView(this)
		val step = TextView(this)
		val distance = TextView(this)
		val maxh = TextView(this)
		val endSpeed = TextView(this)
		num.text = df.format(_num)
		step.text = df.format(_step)
		distance.text = df.format(_distance)
		maxh.text = df.format(_maxh)
		endSpeed.text = df.format(_endSpeed)
		tr.addView(num)
		tr.addView(step)
		tr.addView(distance)
		tr.addView(maxh)
		tr.addView(endSpeed)
		tl.addView(tr, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
	}
}