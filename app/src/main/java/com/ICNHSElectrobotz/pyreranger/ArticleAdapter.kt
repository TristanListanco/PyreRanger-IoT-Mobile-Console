import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ICNHSElectrobotz.pyreranger.ArticleActivity
import com.ICNHSElectrobotz.pyreranger.MainActivity
import com.ICNHSElectrobotz.pyreranger.R

class ArticleAdapter(private val articles: List<MainActivity.Article>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.titleTextView.text = article.title
        holder.descriptionTextView.text = article.description
        holder.cardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ArticleActivity::class.java)
            intent.putExtra("title", article.title)
            intent.putExtra("description", article.description)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.article_card_view)
        val titleTextView: TextView = itemView.findViewById(R.id.article_text_view)
        val descriptionTextView: TextView = itemView.findViewById(R.id.article_description_text_view)
    }
}
