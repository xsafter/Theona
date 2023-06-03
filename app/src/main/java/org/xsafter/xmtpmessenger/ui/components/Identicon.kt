package org.xsafter.xmtpmessenger.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import java.security.MessageDigest
import java.util.Locale

    fun createFromObject(seed: Any): Bitmap {
        return create(Integer.toHexString(seed.hashCode()), Options.DEFAULT)
    }

    fun createFromObject(seed: Any, options: Options): Bitmap {
        return create(Integer.toHexString(seed.hashCode()), options)
    }

    @JvmOverloads
    fun create(seed: String, config: Options = Options.DEFAULT): Bitmap {
        return createBitmap(seed, config.rows, config.size, config.blankColor)
    }

    private fun createBitmap(seed: String, rows: Int, size: Int, blankColor: Int): Bitmap {
        var size = size
        if (size % rows != 0) {
            size = size + (rows - size % rows)
        }
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        val block = size / rows
        val mapping = mapToBit(seed, rows) ?: return bitmap
        val tintColor = getColor(seed)
        val pixels = IntArray(block * block)
        val blankPixels = IntArray(block * block)
        for (p in 0 until block * block) {
            pixels[p] = tintColor
            blankPixels[p] = blankColor
        }
        for (i in 0 until rows) {
            for (j in 0 until rows) {
                if (mapping[i][j]) {
                    bitmap.setPixels(pixels, 0, block, j * block, i * block, block, block)
                } else {
                    bitmap.setPixels(blankPixels, 0, block, j * block, i * block, block, block)
                }
            }
        }
        return bitmap
    }

    fun mapToBit(seed: String, rows: Int): Array<BooleanArray>? {
        checkRows(rows)
        val mapping = Array(rows) {
            BooleanArray(
                rows
            )
        }
        val bits = getHash(seed) ?: return null
        val stride = getStride(rows)
        var index = stride
        for (i in 0 until rows) {
            for (j in 0 until (rows + 1) / 2) {
                val c = bits[index]
                val b = c == '1'
                mapping[i][j] = b
                mapping[i][rows - j - 1] = b
                index += stride
            }
        }
        return mapping
    }

    private fun checkRows(rows: Int) {
        require(rows and 0x1 != 0) { "Argument 'rows' must be an odd number" }
        require(!(rows < 5 || rows > 11)) { "Argument 'rows' must be between 5 and 11" }
    }

    private fun getHash(src: String): CharArray? {
        val md5 = getBinMd5(src)
        return md5?.toCharArray()
    }

    private fun getBinMd5(src: String): String? {
        val encryption = md5(src) ?: return null
        val buffer = StringBuilder()
        for (b in encryption) {
            buffer.append(Integer.toBinaryString((b.toInt() and 0xFF) + 0x100).substring(1))
        }
        return buffer.toString()
    }

    private fun getStride(rows: Int): Int {
        return 128 / (rows * ((rows + 1) / 2))
    }

    private fun md5(src: String): ByteArray? {
        try {
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(src.toByteArray(charset("UTF-8")))
            return md5.digest()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getHexMd5(src: String): String? {
        val encryption = md5(src) ?: return null
        val sb = StringBuilder()
        for (i in encryption.indices) {
            if (Integer.toHexString(0xFF and encryption[i].toInt()).length == 1) {
                sb.append("0").append(Integer.toHexString(0xFF and encryption[i].toInt()))
            } else {
                sb.append(Integer.toHexString(0xFF and encryption[i].toInt()))
            }
        }
        return sb.toString()
    }

    private fun getColor(src: String): Int {
        val md5 = getHexMd5(src)
        if (md5 != null) {
            val rgb = md5.substring(md5.length - 6, md5.length)
            return Color.parseColor("#" + rgb.uppercase(Locale.getDefault()))
        }
        return Color.BLACK
    }

    class Options private constructor(var blankColor: Int, var rows: Int, var size: Int) {
        class Builder {
            var bColor = Color.LTGRAY
            var bRows = 5
            var bSize = 100
            fun setBlankColor(color: Int): Builder {
                bColor = color
                return this
            }

            fun setRows(rows: Int): Builder {
                checkRows(rows)
                bRows = rows
                return this
            }

            fun setSize(size: Int): Builder {
                bSize = size
                return this
            }

            fun create(): Options {
                return Options(bColor, bRows, bSize)
            }

            private fun checkRows(rows: Int) {
                require(rows and 0x1 != 0) { "Argument 'rows' must be an odd number" }
                require(!(rows < 5 || rows > 11)) { "Argument 'rows' must be between 5 and 11" }
            }
        }

        companion object {
            val DEFAULT = Options(Color.LTGRAY, 5, 100)
        }
    }
