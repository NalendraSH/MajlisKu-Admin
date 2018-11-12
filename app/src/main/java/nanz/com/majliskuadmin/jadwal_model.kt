package nanz.com.majliskuadmin

import java.io.Serializable

data class jadwal_model(val poster_url: String? = "", val judul: String? = "",
                        val pemateri: String? = "", val alamat: String? = "", val jadwal: String? = "") : Serializable