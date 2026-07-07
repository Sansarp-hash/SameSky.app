package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.*
import com.example.ui.*
import androidx.compose.ui.draw.shadow
import com.example.api.ContentEntryDto
import com.example.DashboardViewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.JsonClass
import com.example.ui.PollCard

enum class AppLanguage(val code: String, val label: String, val flag: String) {
    EN("en", "English", "🇬🇧"),
    TH("th", "ภาษาไทย", "🇹🇭"),
    ES("es", "Español", "🇪🇸"),
    PT("pt", "Português", "🇧🇷")
}

object TranslationState {
    var currentLanguage = mutableStateOf(AppLanguage.EN)
    
    private val translations = mapOf(
        AppLanguage.EN to mapOf(
            "discover_title" to "Discover",
            "explore_updates" to "Explore the latest updates",
            "search_placeholder" to "Search GL titles or characters...",
            "trending_polls" to "Trending Polls",
            "upcoming_series" to "Upcoming Series",
            "discover_carousel_header" to "Featured Celestial Spotlight 🌌",
            "community_choice" to "Community Choice",
            "view_details" to "View Details",
            "language_label" to "Language",
            "select_language" to "Select Language",
            "marquee_tab" to "Home",
            "discover_tab" to "Discover",
            "community_tab" to "Community",
            "arena_tab" to "Arena",
            "more_hub_tab" to "More Hub",
            "my_profile" to "My Profile",
            "gl_directory" to "GL Directory",
            "career_hub" to "Career Hub",
            "coin_shop" to "Coin Shop",
            "stars_subscription" to "Stars Subscription",
            "leaderboard" to "Leaderboard",
            "series_profile" to "Series Profile",
            "post_thread" to "Post Thread",
            "create_post" to "Create Post"
        ),
        AppLanguage.TH to mapOf(
            "discover_title" to "ค้นพบ",
            "explore_updates" to "สำรวจการอัปเดตล่าสุด",
            "search_placeholder" to "ค้นหาชื่อเรื่องหรือตัวละคร GL...",
            "trending_polls" to "โพลยอดนิยม",
            "upcoming_series" to "ซีรีส์ที่กำลังจะมาถึง",
            "discover_carousel_header" to "สปอตไลท์ดวงดาวเด่น 🌌",
            "community_choice" to "ตัวเลือกของชุมชน",
            "view_details" to "ดูรายละเอียด",
            "language_label" to "ภาษา",
            "select_language" to "เลือกภาษา",
            "marquee_tab" to "หน้าแรก",
            "discover_tab" to "ค้นพบ",
            "community_tab" to "ชุมชน",
            "arena_tab" to "อารีน่า",
            "more_hub_tab" to "ฮับเพิ่มเติม",
            "my_profile" to "โปรไฟล์ของฉัน",
            "gl_directory" to "สารบัญ GL",
            "career_hub" to "ศูนย์รวมอาชีพ",
            "coin_shop" to "ร้านเหรียญ",
            "stars_subscription" to "การสมัครสมาชิกดาว",
            "leaderboard" to "กระดานผู้นำ",
            "series_profile" to "โปรไฟล์ซีรีส์",
            "post_thread" to "กระทู้โพสต์",
            "create_post" to "สร้างโพสต์"
        ),
        AppLanguage.ES to mapOf(
            "discover_title" to "Descubrir",
            "explore_updates" to "Explora las últimas actualizaciones",
            "search_placeholder" to "Buscar títulos o personajes GL...",
            "trending_polls" to "Encuestas Tendencias",
            "upcoming_series" to "Próximas Series",
            "discover_carousel_header" to "Destacado Celestial 🌌",
            "community_choice" to "Elección de la Comunidad",
            "view_details" to "Ver Detalles",
            "language_label" to "Idioma",
            "select_language" to "Seleccionar idioma",
            "marquee_tab" to "Inicio",
            "discover_tab" to "Descubrir",
            "community_tab" to "Comunidad",
            "arena_tab" to "Arena",
            "more_hub_tab" to "Más Hub",
            "my_profile" to "Mi Perfil",
            "gl_directory" to "Directorio GL",
            "career_hub" to "Hub de Carreras",
            "coin_shop" to "Tienda de Monedas",
            "stars_subscription" to "Suscripción Estrellas",
            "leaderboard" to "Tabla de Clasificación",
            "series_profile" to "Perfil de la Serie",
            "post_thread" to "Hilo del Post",
            "create_post" to "Crear Post"
        ),
        AppLanguage.PT to mapOf(
            "discover_title" to "Descobrir",
            "explore_updates" to "Explore as atualizações mais recentes",
            "search_placeholder" to "Buscar títulos ou personagens GL...",
            "trending_polls" to "Enquetes Populares",
            "upcoming_series" to "Próximas Séries",
            "discover_carousel_header" to "Destaque Celestial 🌌",
            "community_choice" to "Escolha da Comunidade",
            "view_details" to "Ver Detalhes",
            "language_label" to "Idioma",
            "select_language" to "Selecionar idioma",
            "marquee_tab" to "Início",
            "discover_tab" to "Descobrir",
            "community_tab" to "Comunidade",
            "arena_tab" to "Arena",
            "more_hub_tab" to "Mais Hub",
            "my_profile" to "Meu Perfil",
            "gl_directory" to "Diretório GL",
            "career_hub" to "Hub de Carreiras",
            "coin_shop" to "Loja de Moedas",
            "stars_subscription" to "Assinatura de Estrelas",
            "leaderboard" to "Tabela de Líderes",
            "series_profile" to "Perfil da Série",
            "post_thread" to "Discussão do Post",
            "create_post" to "Criar Post"
        )
    )

    fun translate(key: String): String {
        return translations[currentLanguage.value]?.get(key) ?: key
    }
}

fun t(key: String): String {
    return TranslationState.translate(key)
}

fun getStableImageUrl(itemName: String): String {
    return when (itemName) {
        "The Secret of Us" -> "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=600&auto=format&fit=crop"
        "GAP The Series", "GAP" -> "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=600&auto=format&fit=crop"
        "23.5" -> "https://images.unsplash.com/photo-1464802686167-b939a6910659?q=80&w=600&auto=format&fit=crop"
        "Blank The Series" -> "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?q=80&w=600&auto=format&fit=crop"
        "Bottoms" -> "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=600&auto=format&fit=crop"
        "Portrait of a Lady on Fire" -> "https://images.unsplash.com/photo-1461344577544-4e5dc948718b?q=80&w=600&auto=format&fit=crop"
        "Carol" -> "https://images.unsplash.com/photo-1481169769263-9121bd443585?q=80&w=600&auto=format&fit=crop"
        "Am I Ok?" -> "https://images.unsplash.com/photo-1501446529957-6226be447c46?q=80&w=600&auto=format&fit=crop"
        "The Handmaiden" -> "https://images.unsplash.com/photo-1508186227413-bb1f5c85844b?q=80&w=600&auto=format&fit=crop"
        "Bad Buddy" -> "https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=600&auto=format&fit=crop"
        "FreenBecky", "Freen & Becky" -> "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=600&auto=format&fit=crop"
        "LingOrm", "Lingling & Orm" -> "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=600&auto=format&fit=crop"
        "Englot", "Engfa & Charlotte" -> "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=600&auto=format&fit=crop"
        else -> "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=600&auto=format&fit=crop"
    }
}

fun getStableAvatarUrl(name: String): String {
    val id = (name.hashCode() % 10).let { if (it < 0) -it else it }
    return when (id) {
        0 -> "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=150&auto=format&fit=crop"
        1 -> "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=150&auto=format&fit=crop"
        2 -> "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=150&auto=format&fit=crop"
        3 -> "https://images.unsplash.com/photo-1544005313-94ddf0286df2?q=80&w=150&auto=format&fit=crop"
        4 -> "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?q=80&w=150&auto=format&fit=crop"
        5 -> "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?q=80&w=150&auto=format&fit=crop"
        6 -> "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?q=80&w=150&auto=format&fit=crop"
        7 -> "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=150&auto=format&fit=crop"
        8 -> "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=150&auto=format&fit=crop"
        else -> "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?q=80&w=150&auto=format&fit=crop"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ProfileViewModel = viewModel()
            val profile by viewModel.profile.collectAsStateWithLifecycle()
            val isDarkMode = profile?.isDarkMode ?: true

            AppTheme(darkTheme = isDarkMode) {
                SameSkyApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Discover : Screen("discover", "Discover", Icons.Filled.Explore)
    object Marquee : Screen("marquee", "Home", Icons.Filled.Home)
    object Community : Screen("community", "Community", Icons.Filled.People)
    object Arena : Screen("arena", "Arena", Icons.Filled.Podcasts)
    object Lists : Screen("lists", "Lists", Icons.AutoMirrored.Filled.List)
    object Subscription : Screen("subscription", "Subscription", Icons.Filled.Star)
    object CoinShop : Screen("coinshop", "Coin Shop", Icons.Filled.MonetizationOn)
    object Leaderboard : Screen("leaderboard", "Leaderboard", Icons.Filled.EmojiEvents)
    object MediaProfile : Screen("media_profile/{mediaId}", "Media", Icons.Filled.Movie) {
        fun createRoute(mediaId: String) = "media_profile/$mediaId"
    }
    object GLDirectory : Screen("gl_directory", "Directory", Icons.Filled.Collections)
    object CareerHub : Screen("career_hub", "Career", Icons.Filled.Work)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object AdminDashboard : Screen("admin_dashboard", "Admin", Icons.Filled.AdminPanelSettings)
    object UserDashboard : Screen("user_dashboard", "Dashboard", Icons.Filled.Dashboard)
    object CreatePost : Screen("create_post", "Create Post", Icons.Filled.Add)
    object MoreHub : Screen("more_hub", "More", Icons.Filled.Apps)
    object Watchlist : Screen("watchlist", "My Watchlist", Icons.Filled.Bookmark)
    object PollArchive : Screen("poll_archive", "Poll Archive", Icons.Filled.HowToVote)
    object AILab : Screen("ai_lab", "AI Lab", Icons.Filled.Science)
    object EncyclopediaCommunity : Screen("encyclopedia_community", "Enc & Comm", Icons.Filled.List)

    object PostDetail : Screen("post_detail/{postId}", "Post", Icons.Filled.Description) {
        fun createRoute(postId: String) = "post_detail/$postId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SameSkyApp() {
    var isOnboarded by remember { mutableStateOf(true) }
    if (!isOnboarded) {
        OnboardingScreen(onComplete = { isOnboarded = true })
        return
    }
    val navController = rememberNavController()
    val viewModel: ProfileViewModel = viewModel()
    val profileState by viewModel.profile.collectAsStateWithLifecycle()
    val isAdmin = profileState?.isAdmin == true
    val items = remember(isAdmin) {
        if (isAdmin) {
            listOf(
                Screen.Marquee,
                Screen.Discover,
                Screen.Community,
                Screen.Arena,
                Screen.MoreHub
            )
        } else {
            listOf(
                Screen.Marquee,
                Screen.Discover,
                Screen.Community,
                Screen.Arena
            )
        }
    }
    var showCreateMenu by remember { mutableStateOf(false) }
    var showCreateTextPostDialog by remember { mutableStateOf(false) }
    var showCreateImagePostDialog by remember { mutableStateOf(false) }
    var showCreateListDialog by remember { mutableStateOf(false) }
    
    var textPostContent by remember { mutableStateOf("") }
    
    var isListBuilderMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<String>() }
    var createdListName by remember { mutableStateOf("") }
    var createdListDesc by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute != Screen.Marquee.route) {
                val pageTitle = when {
                    currentRoute == Screen.Discover.route -> t("discover_tab")
                    currentRoute == Screen.Community.route -> t("community_tab")
                    currentRoute == Screen.Arena.route -> t("arena_tab")
                    currentRoute == Screen.MoreHub.route -> t("more_hub_tab")
                    currentRoute == Screen.Profile.route -> t("my_profile")
                    currentRoute == Screen.GLDirectory.route -> t("gl_directory")
                    currentRoute == Screen.CareerHub.route -> t("career_hub")
                    currentRoute == Screen.CoinShop.route -> t("coin_shop")
                    currentRoute == Screen.Subscription.route -> t("stars_subscription")
                    currentRoute == Screen.Leaderboard.route -> t("leaderboard")
                    currentRoute?.startsWith("media_profile") == true -> t("series_profile")
                    currentRoute?.startsWith("post_detail") == true -> t("post_thread")
                    currentRoute == Screen.CreatePost.route -> t("create_post")
                    else -> ""
                }

                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.navigate(Screen.Profile.route) },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            AsyncImage(
                                model = getStableAvatarUrl("You"),
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, ShimmeringGold, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    title = {
                        Text(
                            text = pageTitle,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            // Language Dropdown Selector Action
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { expanded = true }, modifier = Modifier.size(36.dp).testTag("language_selector_button")) {
                                    Icon(
                                        imageVector = Icons.Filled.Translate,
                                        contentDescription = "Select Language",
                                        tint = ShimmeringGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(WarmObsidian)
                                ) {
                                    AppLanguage.values().forEach { lang ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Text(lang.flag, fontSize = 16.sp)
                                                    Text(
                                                        text = lang.label,
                                                        color = if (TranslationState.currentLanguage.value == lang) ShimmeringGold else Color.White,
                                                        fontWeight = if (TranslationState.currentLanguage.value == lang) FontWeight.Bold else FontWeight.Normal,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            },
                                            onClick = {
                                                TranslationState.currentLanguage.value = lang
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (currentRoute != Screen.Marquee.route && currentRoute != Screen.Profile.route) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clip(RoundedCornerShape(16.dp)).clickable { navController.navigate(Screen.CoinShop.route) }
                                        .background(WarmObsidian)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Filled.Star, "Stars", tint = ShimmeringGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("1,250", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Obsidian)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = WarmObsidian,
                contentColor = LightText,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    val labelText = when (screen) {
                        Screen.Marquee -> t("marquee_tab")
                        Screen.Discover -> t("discover_tab")
                        Screen.Community -> t("community_tab")
                        Screen.Arena -> t("arena_tab")
                        Screen.MoreHub -> t("more_hub_tab")
                        else -> screen.label
                    }
                    NavigationBarItem(
                        icon = { 
                            if (screen == Screen.Community) {
                                BadgedBox(
                                    badge = { Badge(containerColor = ShimmeringGold, contentColor = WarmObsidian) { Text("1") } }
                                ) {
                                    Icon(screen.icon, contentDescription = labelText)
                                }
                            } else {
                                Icon(screen.icon, contentDescription = labelText)
                            }
                        },
                        label = { Text(labelText, fontSize = 10.sp) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShimmeringGold,
                            selectedTextColor = ShimmeringGold,
                            indicatorColor = ShimmeringGold.copy(alpha = 0.15f),
                            unselectedIconColor = MutedText,
                            unselectedTextColor = MutedText
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreatePost.route) },
                containerColor = DustyRose,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("create_post_fab")
            ) {
                Icon(Icons.Filled.Favorite, "Create Post", modifier = Modifier.size(22.dp))
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Marquee.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Marquee.route) { 
                MarqueeScreen(
                    navController = navController,
                    isListBuilderMode = isListBuilderMode,
                    onListBuilderModeChange = { isListBuilderMode = it },
                    selectedItems = selectedItems,
                    createdListName = createdListName,
                    createdListDesc = createdListDesc,
                    onPublishList = { title, desc, items ->
                        val listContent = buildString {
                            append("🌌 New Curated GL List: **$title**\n")
                            if (desc.isNotBlank()) {
                                append("\"$desc\"\n\n")
                            }
                            append("Featured Series:\n")
                            items.forEach { item ->
                                append("✨ $item\n")
                            }
                        }
                        viewModel.addPost(listContent, authorName = "You")
                        viewModel.saveMediaList(title, desc, items)
                        isListBuilderMode = false
                        selectedItems.clear()
                        createdListName = ""
                        createdListDesc = ""
                        navController.navigate(Screen.Community.route)
                    }
                ) 
            }
            composable(Screen.Discover.route) { DiscoverScreen(navController) }
            composable(Screen.Community.route) { CommunityScreen(navController) }
            composable(Screen.Arena.route) { ArenaScreen(navController) }
            composable(Screen.CreatePost.route) { CreatePostScreen(navController, viewModel) }
            composable(Screen.MoreHub.route) { MoreHubScreen(navController) }
            composable(Screen.AILab.route) { com.example.ui.AILabScreen(navController) }
            composable(Screen.EncyclopediaCommunity.route) { EncyclopediaCommunityScreen(navController) }
            composable(Screen.PollArchive.route) { PollArchiveScreen(navController, viewModel) }
            composable(Screen.Watchlist.route) {
                com.example.ui.MovieWatchlistScreen(navController)
            }
            composable(Screen.Subscription.route) { SubscriptionBuilderScreen { navController.popBackStack() } }
            composable(Screen.CoinShop.route) { VirtualCoinShopScreen { navController.popBackStack() } }
            composable(Screen.Leaderboard.route) { LeaderboardScreen() }
            composable(Screen.GLDirectory.route) { GLDirectoryScreen(navController) }
            composable(Screen.CareerHub.route) { CareerHubScreen(navController) }
                composable(Screen.AdminDashboard.route) {
                    com.example.ui.AdminDashboardScreen(navController)
                }
                composable(Screen.UserDashboard.route) {
                    com.example.ui.UserDashboardScreen(navController)
                }

            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.MediaProfile.route) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId") ?: "0"
                MediaProfileScreen(navController, mediaId)
            }
            composable(Screen.PostDetail.route) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: "0"
                PostDetailScreen(navController, postId)
            }
        }
    }

    if (showCreateTextPostDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTextPostDialog = false },
            title = { Text("Share to Community", color = ShimmeringGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Write something about your favorite Girls' Love series...", color = LightText, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = textPostContent,
                        onValueChange = { textPostContent = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShimmeringGold,
                            unfocusedBorderColor = WarmObsidian,
                            focusedTextColor = LightText,
                            unfocusedTextColor = LightText,
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian
                        ),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("What's on your mind? ✨", color = MutedText) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (textPostContent.isNotBlank()) {
                            viewModel.addPost(textPostContent)
                            textPostContent = ""
                            showCreateTextPostDialog = false
                            navController.navigate(Screen.Community.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian)
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCreateTextPostDialog = false }
                ) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = Obsidian,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showCreateImagePostDialog) {
        var imagePostContent by remember { mutableStateOf("") }
        var selectedImagePreset by remember { mutableStateOf("https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=800") }
        
        val presets = listOf(
            "Cosmic Orbit" to "https://images.unsplash.com/photo-1464802686167-b939a6910659?q=80&w=600",
            "Romantic Hearts" to "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=600",
            "Twilight Sky" to "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=600",
            "Two Hands" to "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=600"
        )

        AlertDialog(
            onDismissRequest = { showCreateImagePostDialog = false },
            title = { Text("Create Image Post", color = ShimmeringGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Add text to your image post...", color = LightText, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = imagePostContent,
                        onValueChange = { imagePostContent = it },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShimmeringGold,
                            unfocusedBorderColor = WarmObsidian,
                            focusedTextColor = LightText,
                            unfocusedTextColor = LightText,
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian
                        ),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("E.g., GAP has my whole heart! ❤️", color = MutedText) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select a beautiful theme image:", color = LightText, modifier = Modifier.padding(bottom = 8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { (name, url) ->
                            val isSelected = selectedImagePreset == url
                            Card(
                                onClick = { selectedImagePreset = url },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp),
                                border = BorderStroke(
                                    width = 2.dp,
                                    color = if (isSelected) ShimmeringGold else Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f))
                                    )
                                    Text(
                                        name.split(" ").first(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (imagePostContent.isNotBlank()) {
                            val formatted = "$imagePostContent\n\n🖼️ [Attached Image]($selectedImagePreset)"
                            viewModel.addPost(formatted)
                            imagePostContent = ""
                            showCreateImagePostDialog = false
                            navController.navigate(Screen.Community.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian)
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCreateImagePostDialog = false }
                ) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = Obsidian,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showCreateListDialog) {
        var listNameInput by remember { mutableStateOf("") }
        var listDescInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateListDialog = false },
            title = { Text("Create Curated GL List", color = ShimmeringGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Give your celestial GL list a title and description...", color = LightText, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = listNameInput,
                        onValueChange = { listNameInput = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShimmeringGold,
                            unfocusedBorderColor = WarmObsidian,
                            focusedTextColor = LightText,
                            unfocusedTextColor = LightText,
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian
                        ),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("E.g., Ultimate Healing Dramas 🌌", color = MutedText) },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = listDescInput,
                        onValueChange = { listDescInput = it },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShimmeringGold,
                            unfocusedBorderColor = WarmObsidian,
                            focusedTextColor = LightText,
                            unfocusedTextColor = LightText,
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian
                        ),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("E.g., A collection of series featuring deep emotional healing.", color = MutedText) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (listNameInput.isNotBlank()) {
                            createdListName = listNameInput
                            createdListDesc = listDescInput
                            isListBuilderMode = true
                            selectedItems.clear()
                            showCreateListDialog = false
                            navController.navigate(Screen.Marquee.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian)
                ) {
                    Text("Continue to Selection")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCreateListDialog = false }
                ) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = Obsidian,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun EncyclopediaCommunityScreen(navController: NavHostController) {
    val nestedNavController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            com.example.ui.TogglingNavigationBar(navController = nestedNavController)
        },
        containerColor = Obsidian
    ) { padding ->
        NavHost(
            navController = nestedNavController,
            startDestination = Screen.Discover.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Discover.route) { DiscoverScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.MoreHub.route) { MoreHubScreen(navController) }
        }
    }
}

@JsonClass(generateAdapter = true)
data class MediaItem(
    val title: String,
    val thumbnail: String,
    val region: String,
    val releaseYear: String,
    val description: String = "",
    val priority: String = "Medium"
)

val GL_SERIES_JSON = """
[
  {
    "title": "The Secret of Us",
    "thumbnail": "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=600",
    "region": "Thailand",
    "releaseYear": "2024",
    "description": "A heart-wrenching second-chance romance between a doctor and an actress, adapted from a beloved novel."
  },
  {
    "title": "23.5",
    "thumbnail": "https://images.unsplash.com/photo-1464802686167-b939a6910659?q=80&w=600",
    "region": "Thailand",
    "releaseYear": "2024",
    "description": "A charming high-school romance that starts with an anonymous online crush under the alias Earth."
  },
  {
    "title": "Blank The Series",
    "thumbnail": "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?q=80&w=600",
    "region": "Thailand",
    "releaseYear": "2024",
    "description": "An intense and sophisticated drama revolving around an age-gap romance that defies societal expectations."
  },
  {
    "title": "My Marvellous Dream Is You",
    "thumbnail": "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=600",
    "region": "Thailand",
    "releaseYear": "2024",
    "description": "An ethereal romantic drama exploring childhood friendship blooming into passionate love."
  },
  {
    "title": "Pluto",
    "thumbnail": "https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=600",
    "region": "Thailand",
    "releaseYear": "2024",
    "description": "A mysterious love story featuring a twin sister filling in for her comatose sibling and falling for her lover."
  },
  {
    "title": "GAP The Series",
    "thumbnail": "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=600",
    "region": "Thailand",
    "releaseYear": "2022",
    "description": "The pioneering blockbuster Thai GL series showcasing an office romance between a fresh graduate and her boss."
  }
]
"""

val LESBIAN_SERIES_JSON = """
[
  {
    "title": "Gentleman Jack",
    "thumbnail": "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=600",
    "region": "United Kingdom",
    "releaseYear": "2019",
    "description": "The remarkable story of Anne Lister, a landowner who documents her queer relationships in a secret code."
  },
  {
    "title": "A League of Their Own",
    "thumbnail": "https://images.unsplash.com/photo-1543536448-d209d2d13a1c?q=80&w=600",
    "region": "United States",
    "releaseYear": "2022",
    "description": "The story of women who dream of playing professional baseball, finding deep connection and community."
  },
  {
    "title": "The L Word: Generation Q",
    "thumbnail": "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?q=80&w=600",
    "region": "United States",
    "releaseYear": "2019",
    "description": "A sequel series tracing the lives, loves, and careers of a group of queer women in Los Angeles."
  },
  {
    "title": "Orange Is the New Black",
    "thumbnail": "https://images.unsplash.com/photo-1508186227413-bb1f5c85844b?q=80&w=600",
    "region": "United States",
    "releaseYear": "2013",
    "description": "An epic ensemble drama exploring the lives and diverse, passionate connections inside a women's correctional facility."
  },
  {
    "title": "Feel Good",
    "thumbnail": "https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=600",
    "region": "United Kingdom",
    "releaseYear": "2020",
    "description": "A deeply intimate, semi-autobiographical comedy-drama series following a stand-up comedian navigating love and addiction."
  },
  {
    "title": "Dickinson",
    "thumbnail": "https://images.unsplash.com/photo-1464802686167-b939a6910659?q=80&w=600",
    "region": "United States",
    "releaseYear": "2019",
    "description": "A stylish, modern retelling of Emily Dickinson's life, centering her passionate romance with Sue Gilbert."
  }
]
"""

val GLOBAL_MOVIES_JSON = """
[
  {
    "title": "The Handmaiden",
    "thumbnail": "https://images.unsplash.com/photo-1508186227413-bb1f5c85844b?q=80&w=600",
    "region": "South Korea",
    "releaseYear": "2016",
    "description": "Park Chan-wook's exquisite crime masterpiece depicting a complex romance between an heiress and her maid."
  },
  {
    "title": "Portrait of a Lady on Fire",
    "thumbnail": "https://images.unsplash.com/photo-1461344577544-4e5dc948718b?q=80&w=600",
    "region": "France",
    "releaseYear": "2019",
    "description": "An emotionally intense, award-winning romance between an artist and her aristocratic subject."
  },
  {
    "title": "Carol",
    "thumbnail": "https://images.unsplash.com/photo-1481169769263-9121bd443585?q=80&w=600",
    "region": "United States",
    "releaseYear": "2015",
    "description": "A beautifully shot 1950s period drama about the powerful bond between a mature woman and an aspiring photographer."
  },
  {
    "title": "Kyss Mig",
    "thumbnail": "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?q=80&w=600",
    "region": "Sweden",
    "releaseYear": "2011",
    "description": "A modern Swedish romance depicting the life-altering chemistry between two women who meet at their parents' engagement party."
  },
  {
    "title": "Blue Is the Warmest Color",
    "thumbnail": "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=600",
    "region": "France",
    "releaseYear": "2013",
    "description": "A monumental, Cannes Palme d'Or-winning exploration of a young woman's identity and her deep, tumultuous love story."
  },
  {
    "title": "You Can Live Forever",
    "thumbnail": "https://images.unsplash.com/photo-1501446529957-6226be447c46?q=80&w=600",
    "region": "Canada",
    "releaseYear": "2022",
    "description": "A poignant, atmospheric indie drama about a gay teenager sent to live with her Jehovah's Witness relatives."
  }
]
"""

fun parseMediaItems(json: String): List<MediaItem> {
    return try {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(List::class.java, MediaItem::class.java)
        val adapter = moshi.adapter<List<MediaItem>>(type)
        adapter.fromJson(json) ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@Composable
fun Modifier.romanticHeartThrob(interactionSource: MutableInteractionSource): Modifier {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isAnyActive = isHovered || isPressed

    val infiniteTransition = rememberInfiniteTransition(label = "RomanticPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                1.0f at 0
                1.06f at 150
                1.02f at 300
                1.08f at 450
                1.0f at 800
                1.0f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseScale"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isAnyActive) scale else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "HoverScale"
    )

    return this.graphicsLayer {
        scaleX = animatedScale
        scaleY = animatedScale
    }
}

@Composable
fun MediaGridCard(
    media: MediaItem,
    isSelected: Boolean,
    isListBuilderMode: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isAnyActive = isHovered || isPressed

    val animatedScale by animateFloatAsState(
        targetValue = if (isAnyActive) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "HoverScale"
    )

    val animatedElevation by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isAnyActive) 16.dp else 6.dp,
        animationSpec = tween(durationMillis = 300),
        label = "HoverElevation"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .hoverable(interactionSource)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isAnyActive) ShimmeringGold.copy(alpha = 0.6f) else DustyRose.copy(alpha = 0.4f),
                ambientColor = if (isAnyActive) ShimmeringGold.copy(alpha = 0.3f) else Color.Black
            ),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, ShimmeringGold)
        } else if (isAnyActive) {
            BorderStroke(1.5.dp, ShimmeringGold.copy(alpha = 0.8f))
        } else {
            BorderStroke(1.dp, DustyRose.copy(alpha = 0.2f))
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = media.thumbnail,
                contentDescription = media.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(WarmObsidian),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ShimmeringGold,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    // Try our high-quality stable fallback first! If that's unavailable, fall back to our beautiful gradient placeholder
                    val fallbackUrl = getStableImageUrl(media.title)
                    SubcomposeAsyncImage(
                        model = fallbackUrl,
                        contentDescription = media.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(CosmicViolet.copy(alpha = 0.8f), Obsidian)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Movie,
                                        contentDescription = null,
                                        tint = ShimmeringGold.copy(alpha = 0.6f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "SameSky",
                                        color = ShimmeringGold.copy(alpha = 0.6f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    )
                }
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .padding(top = 12.dp, start = 12.dp)
                    .background(DustyRose.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .border(0.5.dp, DustyRose.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = media.region,
                    color = BlushPink,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp)
                    .background(SoftViolet.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .border(0.5.dp, SoftViolet.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = media.releaseYear,
                    color = Lavender,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = media.title,
                    color = ShimmeringGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = media.description,
                    color = Color(0xFFE2E8F0), // Highly accessible light-gray for pristine contrast and readability
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 3
                )
            }

            if (isListBuilderMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ShimmeringGold else Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.Add,
                        contentDescription = null,
                        tint = if (isSelected) Obsidian else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MediaGridCardSkeleton(modifier: Modifier = Modifier) {
    val shimmer = romanticShimmerBrush()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, ShimmeringGold.copy(alpha = 0.1f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(shimmer)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(shimmer)
                )
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(shimmer)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmer)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(shimmer)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(shimmer)
                )
            }
        }
    }
}

fun ContentEntryDto.toMediaItem() = MediaItem(
    title = this.title,
    thumbnail = this.mediaUrl ?: getStableImageUrl(this.title),
    region = this.sunSign ?: "Global",
    releaseYear = this.mbti ?: "2024",
    description = this.content,
    priority = "Medium"
)

fun getPriorityScore(priority: String): Int {
    return when (priority.lowercase()) {
        "high" -> 3
        "medium" -> 2
        "low" -> 1
        else -> 0
    }
}

@Composable
fun NoSeriesFoundPlaceholder(
    searchQuery: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(DustyRose.copy(alpha = 0.15f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = ShimmeringGold,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Series Found",
                color = ShimmeringGold,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (searchQuery.isNotBlank()) {
                    "We couldn't find any series matching \"$searchQuery\". Try checking your spelling or search for another stellar title under the same sky."
                } else {
                    "No romantic celestial series are currently listed in this category. Connect to database or force sync to update."
                },
                color = LightText.copy(alpha = 0.8f),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            if (searchQuery.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onClearSearch,
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicViolet),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Clear Search ✖",
                        color = ShimmeringGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RomanticNoContentPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(DustyRose.copy(alpha = 0.2f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(120.dp)) {
                    val width = size.width
                    val height = size.height

                    drawCircle(color = ShimmeringGold.copy(alpha = 0.6f), radius = 3f, center = Offset(width * 0.2f, height * 0.2f))
                    drawCircle(color = ShimmeringGold.copy(alpha = 0.8f), radius = 5f, center = Offset(width * 0.8f, height * 0.3f))
                    drawCircle(color = ShimmeringGold.copy(alpha = 0.4f), radius = 4f, center = Offset(width * 0.15f, height * 0.7f))
                    drawCircle(color = ShimmeringGold.copy(alpha = 0.9f), radius = 6f, center = Offset(width * 0.75f, height * 0.8f))

                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(width / 2, height * 0.75f)
                        cubicTo(
                            width * 0.15f, height * 0.45f,
                            width * 0.1f, height * 0.15f,
                            width / 2, height * 0.3f
                        )
                        cubicTo(
                            width * 0.9f, height * 0.15f,
                            width * 0.85f, height * 0.45f,
                            width / 2, height * 0.75f
                        )
                    }
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(BlushPink, DustyRose)
                        )
                    )

                    val starPath = androidx.compose.ui.graphics.Path().apply {
                        val cx = width / 2
                        val cy = height * 0.4f
                        moveTo(cx, cy - 14)
                        lineTo(cx + 4, cy - 4)
                        lineTo(cx + 14, cy)
                        lineTo(cx + 4, cy + 4)
                        lineTo(cx, cy + 14)
                        lineTo(cx - 4, cy + 4)
                        lineTo(cx - 14, cy)
                        lineTo(cx - 4, cy - 4)
                        close()
                    }
                    drawPath(path = starPath, color = ShimmeringGold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Celestial Stories Found",
                color = ShimmeringGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No content matches your selection or search filter right now. Try adjusting your category or search terms to uncover hidden romantic constellations under the SameSky.",
                color = LightText.copy(alpha = 0.7f),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
fun MarqueeScreen(
    navController: NavHostController,
    isListBuilderMode: Boolean,
    onListBuilderModeChange: (Boolean) -> Unit,
    selectedItems: androidx.compose.runtime.snapshots.SnapshotStateList<String>,
    createdListName: String,
    createdListDesc: String,
    onPublishList: (String, String, List<String>) -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    var isViewportOpen by remember { mutableStateOf(true) }
    
    val dashboardViewModel: DashboardViewModel = viewModel()
    val entries by dashboardViewModel.contentEntries.collectAsStateWithLifecycle()
    val supabaseSeriesList by dashboardViewModel.supabaseSeries.collectAsStateWithLifecycle()
    val isFetchingSupabase by dashboardViewModel.isFetchingSupabase.collectAsStateWithLifecycle()
    val hasMoreSupabaseItems by dashboardViewModel.hasMoreSupabaseItems.collectAsStateWithLifecycle()
    val supabaseError by dashboardViewModel.supabaseError.collectAsStateWithLifecycle()
    
    var isFetching by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Thai Series") }
    var searchInputValue by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("Priority") }
    var isSortDropdownExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Thai Series", "World Series", "Movies Across the World")

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchSupabaseSeries()
    }

    LaunchedEffect(searchInputValue) {
        delay(400) // Beautiful debouncing delay
        searchQuery = searchInputValue
    }

    LaunchedEffect(selectedCategory) {
        isFetching = true
        dashboardViewModel.fetchContentEntries(selectedCategory)
        delay(800) // Beautiful buttery romantic shimmer delay
        isFetching = false
    }

    val glSeries = remember { parseMediaItems(GL_SERIES_JSON) }
    val lesbianSeries = remember { parseMediaItems(LESBIAN_SERIES_JSON) }
    val globalMovies = remember { parseMediaItems(GLOBAL_MOVIES_JSON) }

    val activeCombinedItems = remember(selectedCategory, entries, supabaseSeriesList) {
        val fetchedMapped = when (selectedCategory) {
            "Thai Series" -> {
                entries.filter { 
                    it.category.equals("Thai Series", ignoreCase = true) || 
                    it.category.equals("GL Series", ignoreCase = true) || 
                    it.category.equals("gl", ignoreCase = true) || 
                    it.category.equals("gl_series", ignoreCase = true) ||
                    it.sunSign?.contains("Thailand", ignoreCase = true) == true
                }.map { it.toMediaItem() }
            }
            "World Series" -> {
                entries.filter { 
                    it.category.equals("World Series", ignoreCase = true) || 
                    it.category.equals("Lesbian Series", ignoreCase = true) || 
                    it.category.equals("lesbian", ignoreCase = true) || 
                    it.category.equals("lesbian_series", ignoreCase = true)
                }.map { it.toMediaItem() }
            }
            else -> { // Movies Across the World
                entries.filter { 
                    it.category.equals("Movies Across the World", ignoreCase = true) || 
                    it.category.equals("movies", ignoreCase = true) || 
                    it.category.equals("global_movies", ignoreCase = true) ||
                    it.category.equals("movie", ignoreCase = true)
                }.map { it.toMediaItem() }
            }
        }
        val supabaseMapped = supabaseSeriesList.filter {
            when (selectedCategory) {
                "Thai Series" -> it.category?.contains("Thai", ignoreCase = true) == true || it.region?.contains("Thailand", ignoreCase = true) == true || it.category == null
                "World Series" -> it.category?.contains("World", ignoreCase = true) == true || it.category?.contains("Lesbian", ignoreCase = true) == true || (it.category != null && !it.category.contains("Thai", ignoreCase = true) && !it.category.contains("Movie", ignoreCase = true))
                else -> it.category?.contains("Movie", ignoreCase = true) == true
            }
        }.map {
            MediaItem(
                title = it.title,
                thumbnail = it.image_url,
                region = it.region ?: "International",
                releaseYear = it.release_year ?: "2024",
                description = it.description,
                priority = it.priority ?: "Low"
            )
        }
        val localList = when (selectedCategory) {
            "Thai Series" -> glSeries
            "World Series" -> lesbianSeries
            else -> globalMovies
        }
        (fetchedMapped + supabaseMapped + localList).distinctBy { it.title }
    }

    val activeFilteredItems = remember(activeCombinedItems, searchInputValue, sortBy) {
        val filtered = if (searchInputValue.isBlank()) {
            activeCombinedItems
        } else {
            activeCombinedItems.filter {
                it.title.contains(searchInputValue, ignoreCase = true)
            }
        }
        when (sortBy) {
            "Alphabetical" -> filtered.sortedBy { it.title.lowercase() }
            "Priority" -> filtered.sortedByDescending { getPriorityScore(it.priority) }
            else -> filtered
        }
    }

    val savedLists by viewModel.savedMediaLists.collectAsStateWithLifecycle()
    
    val iconRotation by animateFloatAsState(
        targetValue = if (isViewportOpen) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "rotation"
    )

    Box(modifier = Modifier.fillMaxSize().background(Obsidian)) {
        // 1. MAIN VIEWPORT (Global Media Dashboard)
        AnimatedVisibility(
            visible = isViewportOpen,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 400)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 400)),
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Obsidian),
                contentPadding = PaddingValues(bottom = 140.dp) // Generous bottom padding so users can scroll fully past the FAB
            ) {
                if (isListBuilderMode) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicViolet),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, contentDescription = "Star", tint = ShimmeringGold, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Building Custom List:", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(if (createdListName.isNotBlank()) createdListName else "My Selection", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Tap cards below to pin them.", color = LightText, fontSize = 11.sp)
                                    }
                                }
                                Button(
                                    onClick = {
                                        if (selectedItems.isNotEmpty()) {
                                            onPublishList(
                                                if (createdListName.isNotBlank()) createdListName else "My GL Selection",
                                                createdListDesc,
                                                selectedItems.toList()
                                            )
                                        } else {
                                            onListBuilderModeChange(false)
                                            selectedItems.clear()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DustyRose)
                                ) {
                                    Text(if (selectedItems.isNotEmpty()) "Publish (${selectedItems.size})" else "Cancel", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left-aligned avatar with gold border
                            IconButton(
                                onClick = { navController.navigate(Screen.Profile.route) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                AsyncImage(
                                    model = getStableAvatarUrl("You"),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, ShimmeringGold, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            // Branded Title & Subtitle
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "SameSky Media Hub",
                                    color = ShimmeringGold,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    "Exploring global love & romantic narratives",
                                    color = MutedText,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            // Right-aligned header action buttons
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Nested Translate/Language Selector
                                var langMenuExpanded by remember { mutableStateOf(false) }
                                Box {
                                    IconButton(
                                        onClick = { langMenuExpanded = true },
                                        modifier = Modifier.background(WarmObsidian, CircleShape).size(36.dp).testTag("language_selector_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Translate,
                                            contentDescription = "Translate",
                                            tint = ShimmeringGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = langMenuExpanded,
                                        onDismissRequest = { langMenuExpanded = false },
                                        modifier = Modifier.background(WarmObsidian)
                                    ) {
                                        AppLanguage.values().forEach { lang ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Text(lang.flag, fontSize = 16.sp)
                                                        Text(
                                                            text = lang.label,
                                                            color = if (TranslationState.currentLanguage.value == lang) ShimmeringGold else Color.White,
                                                            fontWeight = if (TranslationState.currentLanguage.value == lang) FontWeight.Bold else FontWeight.Normal,
                                                            fontSize = 13.sp
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    TranslationState.currentLanguage.value = lang
                                                    langMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // List Builder Button
                                IconButton(
                                    onClick = { onListBuilderModeChange(true) },
                                    modifier = Modifier.background(WarmObsidian, CircleShape).size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                        contentDescription = "Build List",
                                        tint = ShimmeringGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Modern Search Bar
                item {
                    OutlinedTextField(
                        value = searchInputValue,
                        onValueChange = { searchInputValue = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        placeholder = { Text("Search title, region, or keyword...", color = MutedText, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ShimmeringGold, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchInputValue.isNotEmpty()) {
                                IconButton(onClick = { searchInputValue = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MutedText, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian,
                            focusedBorderColor = ShimmeringGold,
                            unfocusedBorderColor = ShimmeringGold.copy(alpha = 0.3f),
                            focusedTextColor = LightText,
                            unfocusedTextColor = LightText
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                }

                // Dynamic Category Selector Pills - Repositioned right below the Search Bar with beautiful spacing
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp), // Generous vertical breathing room
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 24.dp) // Breathing room, scrolls offscreen with no clipping!
                    ) {
                        items(categories) { category ->
                            val isSel = category == selectedCategory
                            val label = when (category) {
                                "Thai Series" -> "Thai Series 🌸"
                                "World Series" -> "World Series 👩‍❤️‍👩"
                                else -> "Movies Across the World 🌍"
                            }
                            Card(
                                onClick = { selectedCategory = category },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) CosmicViolet else WarmObsidian
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSel) ShimmeringGold else DustyRose.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(horizontal = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSel) ShimmeringGold else LightText,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Supabase Live Sync Indicator - Solves logic state contradiction
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val indicatorColor = if (supabaseError != null) Color(0xFFE84A5F) // Soft Red alert
                                                 else if (isFetchingSupabase) ShimmeringGold 
                                                 else Color(0xFF3ECF8E) // Supabase Brand Green
                            
                            val indicatorText = if (supabaseError != null) "Supabase Connection Alert (Running Offline Mode)"
                                                else if (isFetchingSupabase) "Syncing with Supabase Live..."
                                                else "Connected to Supabase DB ⚡"

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        indicatorColor,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = indicatorText,
                                fontSize = 11.sp,
                                color = indicatorColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Text(
                            text = "FORCE SYNC",
                            fontSize = 10.sp,
                            color = ShimmeringGold,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .clickable { dashboardViewModel.fetchSupabaseSeries() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (isFetchingSupabase) {
                    item {
                        Text(
                            text = "SYNCING CELESTIAL SUPABASE DATABASE... ⚡",
                            color = Color(0xFF3ECF8E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(2) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MediaGridCardSkeleton(modifier = Modifier.weight(1f))
                            MediaGridCardSkeleton(modifier = Modifier.weight(1f))
                        }
                    }
                }

                if (supabaseError != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF33151A)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE84A5F).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Error",
                                        tint = Color(0xFFE84A5F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Database Sync Alert",
                                        color = Color(0xFFE84A5F),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = supabaseError ?: "",
                                    color = LightText,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { dashboardViewModel.fetchSupabaseSeries() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE84A5F)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Text("Retry Connection 🔄", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Interactive Dropdown Sorting Selector & Real-Time Count
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (supabaseError != null) "Sync Alert (Running offline mode)" else if (activeFilteredItems.isEmpty()) "No matching series" else "Matched ${activeFilteredItems.size} series",
                            color = BlushPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Box {
                            Card(
                                onClick = { isSortDropdownExpanded = true },
                                colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Sort,
                                        contentDescription = "Sort Icon",
                                        tint = ShimmeringGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Sort: $sortBy",
                                        color = LightText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow",
                                        tint = ShimmeringGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isSortDropdownExpanded,
                                onDismissRequest = { isSortDropdownExpanded = false },
                                modifier = Modifier.background(WarmObsidian)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Priority (High to Low) 🔥", color = LightText, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        sortBy = "Priority"
                                        isSortDropdownExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Alphabetical (A-Z) 🔠", color = LightText, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        sortBy = "Alphabetical"
                                        isSortDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Horizontally Scrollable Saved Custom Lists
                if (savedLists.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                "MY CURATED COLLECTIONS",
                                color = ShimmeringGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(savedLists) { list ->
                                    Card(
                                        modifier = Modifier
                                            .width(200.dp)
                                            .clickable {
                                                // Pre-fill search with custom list's name/contents
                                                searchInputValue = list.title
                                            },
                                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, DustyRose.copy(alpha = 0.5f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = list.title,
                                                    color = ShimmeringGold,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(
                                                    onClick = { viewModel.deleteSavedMediaList(list.id) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete",
                                                        tint = Color.Red.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                            if (list.description.isNotBlank()) {
                                                Text(
                                                    text = list.description,
                                                    color = LightText,
                                                    fontSize = 10.sp,
                                                    maxLines = 1
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${list.itemsJson.split(",").size} series pinned",
                                                color = MutedText,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isFetching) {
                    item {
                        Text(
                            text = "${selectedCategory.uppercase()} 🌸",
                            color = ShimmeringGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(3) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MediaGridCardSkeleton(modifier = Modifier.weight(1f))
                            MediaGridCardSkeleton(modifier = Modifier.weight(1f))
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "${selectedCategory.uppercase()} ✨",
                            color = ShimmeringGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    if (activeFilteredItems.isEmpty()) {
                        item {
                            NoSeriesFoundPlaceholder(
                                searchQuery = searchInputValue,
                                onClearSearch = { searchInputValue = "" },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        val chunkedItems = activeFilteredItems.chunked(2)
                        items(chunkedItems) { rowItems ->
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                for (media in rowItems) {
                                    val isSelected = selectedItems.contains(media.title)
                                    Box(modifier = Modifier.weight(1f)) {
                                        MediaGridCard(
                                            media = media,
                                            isSelected = isSelected,
                                            isListBuilderMode = isListBuilderMode,
                                            onClick = {
                                                if (isListBuilderMode) {
                                                    if (isSelected) {
                                                        selectedItems.remove(media.title)
                                                    } else {
                                                        selectedItems.add(media.title)
                                                    }
                                                } else {
                                                    navController.navigate(Screen.MediaProfile.createRoute(media.title))
                                                }
                                            }
                                        )
                                    }
                                }
                                if (rowItems.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    if (hasMoreSupabaseItems && !isFetchingSupabase && activeFilteredItems.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { dashboardViewModel.fetchSupabaseSeries() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CosmicViolet.copy(alpha = 0.6f)),
                                    border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.3f)),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.height(40.dp).testTag("load_more_button")
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Refresh,
                                            contentDescription = "Load More",
                                            tint = ShimmeringGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Load More Series 🌌",
                                            color = ShimmeringGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. STUNNING MINIMIZED CELESTIAL LANDING VIEWPORT
        AnimatedVisibility(
            visible = !isViewportOpen,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 400)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 400)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(CosmicViolet.copy(alpha = 0.3f), Obsidian),
                            center = Offset(700f, 1400f),
                            radius = 1200f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Public,
                        contentDescription = null,
                        tint = ShimmeringGold,
                        modifier = Modifier
                            .size(72.dp)
                            .graphicsLayer {
                                rotationZ = iconRotation
                            }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "SameSky Media Hub",
                        color = ShimmeringGold,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Portal is currently minimized.\nTap the custom cosmic anchor in the bottom-right corner to unfold the database.",
                        color = LightText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    if (savedLists.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "🌌 Your Saved Collections",
                            color = ShimmeringGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(savedLists) { list ->
                                Card(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .clickable {
                                            searchQuery = list.title
                                            isViewportOpen = true
                                        },
                                    colors = CardDefaults.cardColors(containerColor = WarmObsidian.copy(alpha = 0.8f)),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(0.5.dp, ShimmeringGold.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = list.title,
                                            color = ShimmeringGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${list.itemsJson.split(",").size} pinned series",
                                            color = MutedText,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DustyRose))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ShimmeringGold))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SoftViolet))
                    }
                }
            }
        }

        // 3. CORNER-ANCHORED FLOATING BOTTOM NAVIGATION CONTROLLER (Bottom Right Corner)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .size(64.dp)
        ) {
            FloatingActionButton(
                onClick = { isViewportOpen = !isViewportOpen },
                containerColor = if (isViewportOpen) DustyRose else ShimmeringGold,
                contentColor = Obsidian,
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("corner_nav_toggle"),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = if (isViewportOpen) Icons.Filled.Close else Icons.Filled.Explore,
                    contentDescription = if (isViewportOpen) "Close Portal" else "Open Portal",
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer {
                            rotationZ = iconRotation
                        },
                    tint = Obsidian
                )
            }
        }
    }
}

@Composable
fun MediaRow(
    title: String, 
    items: List<String>, 
    tintColor: Color, 
    navController: NavHostController, 
    isListBuilderMode: Boolean = false, 
    selectedItems: MutableList<String>? = null
) {
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    
    rememberIntersectionObserver(lazyListState = listState) {
        // lazy load triggered
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            title,
            color = LightText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(state = listState, contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { item ->
                val isSelected = selectedItems?.contains(item) == true
                Card(
                    onClick = {
                        if (isListBuilderMode && selectedItems != null) {
                            if (isSelected) selectedItems.remove(item) else selectedItems.add(item)
                        } else {
                            navController.navigate(Screen.MediaProfile.createRoute(item))
                        }
                    },
                    modifier = Modifier.width(140.dp).height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) BorderStroke(2.dp, ShimmeringGold) else null
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = getStableImageUrl(item),
                            contentDescription = item,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.4f)))
                        Text(
                            item, 
                            color = LightText, 
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                        )
                        if (isListBuilderMode) {
                            Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp).clip(CircleShape).background(if (isSelected) ShimmeringGold else Color.Black.copy(alpha=0.5f)), contentAlignment = Alignment.Center) {
                                if (isSelected) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Obsidian, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CarouselSlide(
    val id: String,
    val title: String,
    val subtitle: String,
    val yearInfo: String,
    val imageUrl: String,
    val description: String,
    val tag: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverCarousel(navController: NavHostController) {
    val carouselSlides = remember {
        listOf(
            CarouselSlide(
                id = "LingOrm",
                title = "The Secret of Us",
                subtitle = "Lingling Kwong & Orm Kornnaphat",
                yearInfo = "2024 • Thailand • 8 Episodes",
                imageUrl = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=800&auto=format&fit=crop",
                description = "A beautiful romantic drama about Dr. Fahlada and Earn who reunite years after a painful separation. Filled with tension, emotional healing, and professional conflict.",
                tag = "CRITICS' CHOICE 🏆"
            ),
            CarouselSlide(
                id = "FreenBecky",
                title = "GAP The Series",
                subtitle = "Freen Sarocha & Becky Armstrong",
                yearInfo = "2022 • Thailand • 12 Episodes",
                imageUrl = "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=800&auto=format&fit=crop",
                description = "The groundbreaking first full Thai GL series, telling a delightful story of a young office worker Mon who is infatuated with Sam, her high-profile boss. A massive global phenomenon.",
                tag = "GLOBAL HIT 🔥"
            ),
            CarouselSlide(
                id = "Englot",
                title = "Show Me Love",
                subtitle = "Engfa Waraha & Charlotte Austin",
                yearInfo = "2023 • Thailand • 10 Episodes",
                imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=800&auto=format&fit=crop",
                description = "A romantic comedy-drama about Meena, a small-town girl who journeys to Bangkok to follow her dreams and encounters Sherene, an aspiring beauty queen.",
                tag = "CELESTIAL FAVORITE 💫"
            ),
            CarouselSlide(
                id = "Blank",
                title = "Blank The Series",
                subtitle = "Faye Peraya & Yoko Apasra",
                yearInfo = "2024 • Thailand • 12 Episodes",
                imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=800&auto=format&fit=crop",
                description = "A touching and intense romance between Khun Nueng and Aneung, dealing with age gap, societal expectations, and healing through pure connection.",
                tag = "TRENDING ⚡"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { carouselSlides.size })

    // Auto-scroll logic
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            if (pagerState.pageCount > 0) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = t("discover_carousel_header"),
            color = ShimmeringGold,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .testTag("discover_carousel"),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            val slide = carouselSlides[page]
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { navController.navigate(Screen.MediaProfile.createRoute(slide.id)) },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.25f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = slide.imageUrl,
                        contentDescription = slide.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Obsidian.copy(alpha = 0.4f),
                                        Obsidian.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    // Text & Actions info
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Tag at top left
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = slide.tag,
                                color = ShimmeringGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Obsidian.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                    .border(0.5.dp, ShimmeringGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )

                            // Swipe tip label
                            Text(
                                text = "${page + 1}/${carouselSlides.size}",
                                color = LightText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Obsidian.copy(alpha = 0.6f), CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Title / Subtitle at bottom
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = slide.title,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = slide.subtitle,
                                color = ShimmeringGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = slide.description,
                                color = LightText.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                maxLines = 2,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .background(ShimmeringGold, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Obsidian,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = t("view_details"),
                                    color = Obsidian,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dot Page Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            carouselSlides.forEachIndexed { index, _ ->
                val isSelected = pagerState.currentPage == index
                val width by animateFloatAsState(
                    targetValue = if (isSelected) 18f else 6f,
                    animationSpec = tween(300),
                    label = "IndicatorWidth"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(width.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ShimmeringGold else LightText.copy(alpha = 0.3f))
                )
            }
        }
    }
}

@Composable
fun CelestialMatchWidget() {
    val starSigns = listOf("Aries ♈", "Taurus ♉", "Gemini ♊", "Cancer ♋", "Leo ♌", "Virgo ♍", "Libra ♎", "Scorpio ♏", "Sagittarius ♐", "Capricorn ♑", "Aquarius ♒", "Pisces ♓")
    var selectedSign by remember { mutableStateOf("Aries ♈") }
    var matchPercentage by remember { mutableStateOf(85) }
    var matchedCharacter by remember { mutableStateOf("Dr. Fahlada 🩺") }
    var showMatchResult by remember { mutableStateOf(false) }
    var expandedSignDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSign) {
        val hash = selectedSign.hashCode().coerceAtLeast(0)
        matchPercentage = 75 + (hash % 25)
        matchedCharacter = when (hash % 5) {
            0 -> "Dr. Fahlada 🩺 (The Secret of Us)"
            1 -> "Sam Kornnaphat 💼 (GAP The Series)"
            2 -> "Khun Nueng 🎨 (Blank The Series)"
            3 -> "Sun ☀️ (23.5 The Series)"
            else -> "Earn 🌸 (The Secret of Us)"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("celestial_match_card"),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CosmicViolet.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Celestial GL Matcher ✨",
                    color = ShimmeringGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicViolet.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "NEW IDEA 💡",
                        color = LightText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = "Calculate your daily romantic synergy with prominent GL heroines based on your star sign alignment under SameSky.",
                color = LightText.copy(alpha = 0.8f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select your Sign:",
                    color = MutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Box {
                    Button(
                        onClick = { expandedSignDropdown = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Obsidian),
                        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedSign, color = LightText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = ShimmeringGold, modifier = Modifier.size(14.dp))
                        }
                    }

                    DropdownMenu(
                        expanded = expandedSignDropdown,
                        onDismissRequest = { expandedSignDropdown = false },
                        modifier = Modifier.background(WarmObsidian).heightIn(max = 200.dp)
                    ) {
                        starSigns.forEach { sign ->
                            DropdownMenuItem(
                                text = { Text(sign, color = Color.White, fontSize = 12.sp) },
                                onClick = {
                                    selectedSign = sign
                                    expandedSignDropdown = false
                                    showMatchResult = true
                                }
                            )
                        }
                    }
                }
            }

            if (showMatchResult) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Obsidian),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "💖 Synergy Calculated: $matchPercentage% 💖",
                            color = ShimmeringGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Your starry alignment today points to:",
                            color = MutedText,
                            fontSize = 11.sp
                        )
                        Text(
                            text = matchedCharacter,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Under the SameSky, your paths converge under favorable constellations. Healing and deep heart-to-heart conversations await you both!",
                            color = LightText.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                Button(
                    onClick = { showMatchResult = true },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicViolet),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Calculate Alignment 🪐", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun DiscoverScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedQuery = searchQuery
        showSuggestions = debouncedQuery.isNotBlank()
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Obsidian).padding(top = 16.dp)) {
        item {
            Text(t("discover_title"), color = ShimmeringGold, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp))
            
            // Elegant Carousel
            DiscoverCarousel(navController)
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) { 
                Text(t("explore_updates"), color = MutedText, fontSize = 12.sp)
                Icon(Icons.Filled.EmojiEvents, contentDescription = "Leaderboard", tint = ShimmeringGold, modifier = Modifier.clickable { navController.navigate(Screen.Leaderboard.route) }) 
            }
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text(t("search_placeholder"), color = MutedText) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = ShimmeringGold) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShimmeringGold,
                    unfocusedBorderColor = WarmObsidian,
                    focusedTextColor = LightText,
                    unfocusedTextColor = LightText,
                    focusedContainerColor = WarmObsidian,
                    unfocusedContainerColor = WarmObsidian
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            if (showSuggestions) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Search results for '$debouncedQuery'", color = ShimmeringGold, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                        listOf("The Secret of Us", "Blank The Series", "23.5").filter { it.contains(debouncedQuery, ignoreCase = true) }.let { results ->
                            if (results.isEmpty()) Text("No results found.", color = MutedText, modifier = Modifier.padding(8.dp))
                            else results.forEach { res ->
                                Text(res, color = LightText, modifier = Modifier.fillMaxWidth().clickable { showSuggestions = false; searchQuery = res }.padding(8.dp))
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Celestial signs matcher widget
            CelestialMatchWidget()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sorting Dropdown
            var expanded by remember { mutableStateOf(false) }
            var sortOption by remember { mutableStateOf("Date Added") }
            val sortOptions = listOf("Date Added", "Popularity", "Alphabetical")
            
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = WarmObsidian)
                ) {
                    Text("Sort by: $sortOption", color = ShimmeringGold)
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = ShimmeringGold)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Obsidian)
                ) {
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = LightText) },
                            onClick = { 
                                sortOption = option
                                expanded = false 
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Media Type Filtering
            var selectedType by remember { mutableStateOf("All") }
            val mediaTypes = listOf("All", "Movie", "Series", "Drama")

            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                mediaTypes.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = WarmObsidian,
                            selectedContainerColor = ShimmeringGold.copy(alpha = 0.2f),
                            labelColor = LightText,
                            selectedLabelColor = ShimmeringGold
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { navController.navigate(Screen.Watchlist.route) }
                    .testTag("discover_watchlist_cta_card"),
                colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ShimmeringGold.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Movie, "TMDb Watchlist", tint = ShimmeringGold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TMDb Watchlist Hub", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Search global movies & build your curated collection.", color = MutedText, fontSize = 12.sp)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Open Watchlist", tint = ShimmeringGold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(Icons.Filled.BarChart, contentDescription = null, tint = SoftViolet)
                Spacer(modifier = Modifier.width(8.dp))
                Text(t("trending_polls"), color = LightText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(3) { index ->
                    PollKanbanCard(index)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(Icons.Filled.Movie, contentDescription = null, tint = SoftViolet)
                Spacer(modifier = Modifier.width(8.dp))
                Text(t("upcoming_series"), color = LightText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(2) { index ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = WarmObsidian)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("New GL Drama $index", color = LightText, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Coming to screens later this year. A heartwarming story about...", color = MutedText, fontSize = 14.sp)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun PollKanbanCard(index: Int) {
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val category = if (index == 0) "SHIPS" else if (index == 1) "TROPES" else "AWARDS"
            val question = if (index == 0) "Who is the most iconic ship of 2026?" else if (index == 1) "Favorite High School GL Trope?" else "Best Series OST?"
            
            Text(category, color = SoftViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(SoftViolet.copy(alpha=0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
            Text(question, color = LightText, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))
            
            if (index == 0) {
                PollOption("LingOrm", 65)
                PollOption("FreenBecky", 20)
                PollOption("MilkLove", 15)
            } else if (index == 1) {
                PollOption("Enemies to Lovers", 50)
                PollOption("Childhood Friends", 30)
                PollOption("Fake Dating", 20)
            } else {
                PollOption("Blank The Series OST", 45)
                PollOption("23.5 OST", 35)
                PollOption("GAP The Series OST", 20)
            }
        }
    }
}

@Composable
fun PollOption(name: String, percentage: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Obsidian)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percentage / 100f)
                .height(36.dp)
                .background(SoftViolet.copy(alpha = 0.3f))
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, color = LightText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("$percentage%", color = ShimmeringGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        border = BorderStroke(1.dp, Color.DarkGray)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("AD", color = MutedText, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopEnd))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Sponsored: Exclusive Fan Merch 50% Off!", color = LightText, fontWeight = FontWeight.Bold)
                Text("Upgrade to Premium for an ad-free experience.", color = MutedText, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun CommunityScreen(navController: NavHostController, viewModel: ProfileViewModel = viewModel()) {
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val likedPosts = remember { mutableStateListOf<String>() }

    // Intersection observer utility to prevent lag
    rememberIntersectionObserver(lazyListState = listState) {
        // trigger loading more posts
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize().background(Obsidian).padding(horizontal = 16.dp)) {
        item {
            Text("Community Feed", color = ShimmeringGold, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
            com.example.ui.TrendingPollsChart()
            Spacer(modifier = Modifier.height(8.dp))
            AdBanner()
            Spacer(modifier = Modifier.height(16.dp))
            PollCard(
                pollId = "poll_1",
                currentUserId = profile?.id?.toString(),
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(
                onClick = { navController.navigate(Screen.PollArchive.route) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("View Poll Archive", color = LightText, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(posts) { post ->
            val isLiked = likedPosts.contains(post.id)
            val upvotesCount = post.upvotes + if (isLiked) 1 else 0
            val authorTier = post.author?.tier ?: "Free"

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { navController.navigate(Screen.PostDetail.createRoute(post.id)) },
                colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(model = getStableAvatarUrl(post.author?.name ?: post.id), contentDescription = "Avatar", modifier = Modifier.size(48.dp).clip(CircleShape).background(Lavender), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(post.author?.name ?: "Unknown", color = LightText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (authorTier != "Free") {
                                        Box(modifier = Modifier.background(ShimmeringGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                            Text(authorTier, color = ShimmeringGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (post.author?.isAdmin == true) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Filled.AdminPanelSettings, "Admin", tint = CosmicViolet, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text("2 hours ago", color = MutedText, fontSize = 12.sp)
                            }
                        }
                        
                        if (profile?.isModerator == true) {
                            IconButton(onClick = { /* mod actions */ }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.MoreVert, "Moderate", tint = MutedText)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val videoParts = post.content.split("\n\n📹 [Attached Video](")
                    val hasVideo = videoParts.size > 1
                    val contentWithoutVideo = videoParts[0]
                    val videoUrl = if (hasVideo) videoParts[1].replace(")", "") else null

                    val imageParts = contentWithoutVideo.split("\n\n🖼️ [Attached Image](")
                    val hasImage = imageParts.size > 1
                    val displayText = imageParts[0]
                    val imageUrl = if (hasImage) imageParts[1].replace(")", "") else null

                    Text(displayText, color = LightText, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                    
                    imageUrl?.let { url ->
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = url,
                            contentDescription = "Attached Image",
                            modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    videoUrl?.let { url ->
                        Spacer(modifier = Modifier.height(12.dp))
                        VideoPlayerPlaceholder(url = url)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (isLiked) likedPosts.remove(post.id) else likedPosts.add(post.id)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (isLiked) DustyRose else MutedText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$upvotesCount", color = MutedText, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(24.dp))
                        Icon(Icons.Filled.ChatBubbleOutline, "Comment", tint = MutedText, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${post.comments?.size ?: 0}", color = MutedText, fontSize = 14.sp)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun ArenaScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Live", "Events", "Trivia", "Watch Party")

    Column(modifier = Modifier.fillMaxSize().background(Obsidian)) {
        Text("GL Arena", color = ShimmeringGold, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            AdBanner()
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                onClick = { navController.navigate(Screen.Leaderboard.route) },
                modifier = Modifier.weight(1f).height(46.dp),
                colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = "Leaderboard", tint = ShimmeringGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Leaderboard", color = LightText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Card(
                onClick = { navController.navigate(Screen.Subscription.route) },
                modifier = Modifier.weight(1f).height(46.dp),
                colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DustyRose.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Star, contentDescription = "Subscription", tint = DustyRose, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Star Premium", color = LightText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Obsidian,
            contentColor = ShimmeringGold,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ShimmeringGold
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, color = if (selectedTab == index) ShimmeringGold else MutedText, fontWeight = FontWeight.Bold) }
                )
            }
        }
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (selectedTab == 0) { // Live Streams
                items(2) { index ->
                    var showGiftTray by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { showGiftTray = !showGiftTray },
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.PlayCircleOutline, "Play", modifier = Modifier.size(64.dp), tint = ShimmeringGold)
                                Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(Color.Red, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("LIVE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                com.example.ui.RomanticSlideIn(visible = showGiftTray, modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp)) {
                                    Card(colors = CardDefaults.cardColors(containerColor = CosmicViolet), shape = RoundedCornerShape(12.dp)) {
                                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Send a Gift", color = Color.White, fontWeight = FontWeight.Bold)
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Icon(Icons.Filled.Favorite, contentDescription = "Heart", tint = BlushPink)
                                                Icon(Icons.Filled.Star, contentDescription = "Star", tint = ShimmeringGold)
                                                Icon(Icons.Filled.FavoriteBorder, contentDescription = "Diamond", tint = Color.Cyan)
                                            }
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Q&A with the Cast", color = LightText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("1.2k watching now", color = MutedText, fontSize = 14.sp)
                            }
                        }
                    }
                }
            } else {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("${tabs[selectedTab]} coming soon...", color = MutedText)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Astrology", "Tarot", "MBTI", "Favorites")

    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val dailyHoroscope by viewModel.dailyHoroscope.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    var showEditFavorites by remember { mutableStateOf(false) }
    val isHoroscopeLoading by viewModel.isHoroscopeLoading.collectAsStateWithLifecycle()
    val drawnCards by viewModel.drawnCards.collectAsStateWithLifecycle()
    val tarotReading by viewModel.tarotReading.collectAsStateWithLifecycle()
    val isTarotLoading by viewModel.isTarotLoading.collectAsStateWithLifecycle()
    val mbtiAnswers by viewModel.mbtiAnswers.collectAsStateWithLifecycle()

    // Sign selector dialog state
    var showSignPicker by remember { mutableStateOf(false) }
    var activeSignType by remember { mutableStateOf("Sun") } // "Sun", "Moon", "Rising"

    val zodiacSigns = listOf(
        "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
        "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    )

    val mbtiTropes = mapOf(
        "INFJ" to Pair("The Mystical Soulmate Finder", "You analyze every micro-expression and adore deep, intense romance like 'The Secret of Us'. You live for profound, slow-burn soul bonds."),
        "ENFP" to Pair("The Sunshine Protagonist", "You are the ultimate ray of sunshine, hyping up every cute scene, writing excited theories, and bringing joyful energy to the community!"),
        "INTJ" to Pair("The Mastermind Director", "You map out complex storylines and predict every plot twist before it happens. You deeply appreciate high-stakes psychological layers."),
        "INFP" to Pair("The Romantic Dreamer", "You write gorgeous fan-fiction in your head, feel every emotional scene deeply, and root endlessly for a beautiful happily-ever-after."),
        "ENFJ" to Pair("The Devoted Caregiver", "You passionately defend your favorite couples and act as the emotional anchor for the entire GL fandom. You love nurturing dynamics."),
        "ENTP" to Pair("The Playful Instigator", "You live for cheeky bantering and love a playful enemies-to-lovers storyline! You enjoy debating theories and character motives."),
        "ISTJ" to Pair("The Dedicated Archivist", "You keep immaculate lists of every GL series ever released and value structured episode schedules. You respect canon details deeply."),
        "ISFJ" to Pair("The Gentle Guardian", "You love soft, domestic pairings and pure, comforting romances. You are always there to support fellow fans with kindness."),
        "ESTP" to Pair("The Chaotic Wingwoman", "You live for fast-paced drama, high energy, and instant sparks! You're always encouraging characters to make bold romantic moves."),
        "ESFP" to Pair("The Social Monarch", "You host legendary watch-parties and always root for the most glamorous, high-profile couples in the spotlight!"),
        "INTP" to Pair("The Analytical Observer", "You dissect the editing, pacing, and cinematography of shows, enjoying logical character development and subtle subtext."),
        "ENTJ" to Pair("The Power-Couple Enthusiast", "You respect strong, independent female leads who build empires together. High-society romance is your absolute favorite."),
        "ISFP" to Pair("The Artistic Aesthetic", "You have a deep appreciation for beautiful color palettes, aesthetic shots, and romantic acoustic soundtracks. You love art-centric pairings."),
        "ESTJ" to Pair("The Strict Moderator", "You love clean plots, sensible character decisions, and organized schedules. You keep community theories logical and well-structured."),
        "ESFJ" to Pair("The Cozy Matchmaker", "You love arranging cute pairings in your mind and sharing soft, heartfelt moments with friends. Comfort shows are your go-to."),
        "ISTP" to Pair("The Quiet Pragmatist", "You appreciate action-packed plotlines and characters who express their affection through actions rather than sweet words.")
    )

    LaunchedEffect(profile) {
        if (profile?.sunSign == null && !isHoroscopeLoading && dailyHoroscope == null) {
            viewModel.updateProfile(sunSign = "Scorpio", moonSign = "Pisces", risingSign = "Leo")
        }
        if (profile?.sunSign != null && dailyHoroscope == null && !isHoroscopeLoading) {
            viewModel.fetchHoroscope(profile!!.sunSign!!)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(horizontal = 16.dp)
    ) {
        // Modernized header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(CosmicViolet, DustyRose)
                        )
                    )
            ) {
                // Glow accent
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(WarmObsidian)
                        .border(3.dp, ShimmeringGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = getStableAvatarUrl("You"),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(94.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "GL Fanatic",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@glfanatic • Cosmic Watcher",
                    color = ShimmeringGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarmObsidian)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("120", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Followers", color = LightText, fontSize = 12.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarmObsidian)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("45", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Following", color = LightText, fontSize = 12.sp)
                    }
                }
            }
        }

        // Action Hub links in modern grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        onClick = { navController.navigate(Screen.Subscription.route) },
                        modifier = Modifier.weight(1f).height(65.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Star, "Premium", tint = ShimmeringGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Premium Access", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Get stars & badges", color = LightText, fontSize = 10.sp)
                            }
                        }
                    }

                    Card(
                        onClick = { navController.navigate(Screen.CareerHub.route) },
                        modifier = Modifier.weight(1f).height(65.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SoftViolet.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Work, "Career", tint = SoftViolet, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Career Hub", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Find writer roles", color = LightText, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        onClick = { navController.navigate(Screen.UserDashboard.route) },
                        modifier = Modifier.weight(1f).height(65.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DustyRose.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Dashboard, "Dashboard", tint = DustyRose, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("User Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Track pairings", color = LightText, fontSize = 10.sp)
                            }
                        }
                    }

                    Card(
                        onClick = { navController.navigate(Screen.AdminDashboard.route) },
                        modifier = Modifier.weight(1f).height(65.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CosmicViolet.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.AdminPanelSettings, "Admin", tint = CosmicViolet, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Admin Control", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Moderate universe", color = LightText, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // My Cosmic Favorites Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-15).dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Favorite, "Favorites", tint = DustyRose, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("My Cosmic Favorites", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = { showEditFavorites = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Edit, "Edit Favorites", tint = ShimmeringGold, modifier = Modifier.size(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Favorite Series
                    Card(
                        modifier = Modifier.weight(1f).height(90.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Movie, "Series", tint = ShimmeringGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("FAVORITE SERIES", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(profile?.favoriteSeries ?: "Blank: The Series", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Favorite Couple
                    Card(
                        modifier = Modifier.weight(1f).height(90.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DustyRose.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.People, "Couple", tint = DustyRose, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("FAVORITE COUPLE", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(profile?.favoriteCouple ?: "FayeYoko", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Favorite Actress
                    Card(
                        modifier = Modifier.weight(1f).height(90.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Lavender.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, "Actress", tint = Lavender, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("FAVORITE ACTRESS", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(profile?.favoriteActress ?: "Faye Peraya", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Bias
                    Card(
                        modifier = Modifier.weight(1f).height(90.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SoftViolet.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.FavoriteBorder, "Bias", tint = SoftViolet, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("MY BIAS", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(profile?.bias ?: "Yoko Apasra", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Tabs Selector Row
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-10).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WarmObsidian)
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DustyRose else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) Color.White else MutedText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Active Tabs Content
        if (selectedTab == 0) { // Astrology Tab
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, "Astrology", tint = ShimmeringGold, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Your Celestial GL Chart", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Tap any sign below to customize your astrological energies! ✨",
                            color = LightText,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Sun Sign Card
                        Card(
                            onClick = {
                                activeSignType = "Sun"
                                showSignPicker = true
                            },
                            colors = CardDefaults.cardColors(containerColor = Obsidian),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            border = BorderStroke(1.dp, DustyRose.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.WbSunny, "Sun", tint = DustyRose, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("SUN SIGN", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(profile?.sunSign ?: "Select Sign", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Change", color = DustyRose, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Edit", tint = DustyRose, modifier = Modifier.size(12.dp))
                                }
                            }
                        }

                        // Moon Sign Card
                        Card(
                            onClick = {
                                activeSignType = "Moon"
                                showSignPicker = true
                            },
                            colors = CardDefaults.cardColors(containerColor = Obsidian),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            border = BorderStroke(1.dp, Lavender.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Brightness3, "Moon", tint = Lavender, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("MOON SIGN", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(profile?.moonSign ?: "Select Sign", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Change", color = Lavender, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Edit", tint = Lavender, modifier = Modifier.size(12.dp))
                                }
                            }
                        }

                        // Rising Sign Card
                        Card(
                            onClick = {
                                activeSignType = "Rising"
                                showSignPicker = true
                            },
                            colors = CardDefaults.cardColors(containerColor = Obsidian),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            border = BorderStroke(1.dp, SoftViolet.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.TrendingUp, "Rising", tint = SoftViolet, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("RISING SIGN", color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(profile?.risingSign ?: "Select Sign", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Change", color = SoftViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Edit", tint = SoftViolet, modifier = Modifier.size(12.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Obsidian, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Stars, "Insight", tint = ShimmeringGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Daily Cosmic Romance Advice", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (isHoroscopeLoading) {
                            ShimmerLoading(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(8.dp)))
                        } else {
                            Text(
                                text = dailyHoroscope ?: "The alignment is loading...",
                                color = LightText,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Obsidian)
                                    .padding(14.dp)
                            )
                        }
                    }
                }
            }
        } else if (selectedTab == 1) { // Tarot Tab
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SoftViolet.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.FilterNone, "Tarot", tint = ShimmeringGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("SameSky Tarot Oracle", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (drawnCards.isEmpty() && !isTarotLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Obsidian)
                                    .clickable { viewModel.drawTarotCards() },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Favorite, "Heart", tint = DustyRose, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Reveal Your Romantic Destiny", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Tap to pull 3 cosmic tarot cards", color = MutedText, fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.drawTarotCards() },
                                colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text("Pull Tarot Cards", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                drawnCards.forEachIndexed { idx, cardName ->
                                    var isFlipped by remember { mutableStateOf(false) }
                                    LaunchedEffect(cardName) {
                                        delay(idx * 250L) // Staggered flip
                                        isFlipped = true
                                    }

                                    val rotation by animateFloatAsState(
                                        targetValue = if (isFlipped) 180f else 0f,
                                        animationSpec = tween(durationMillis = 500)
                                    )

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(130.dp)
                                            .graphicsLayer {
                                                rotationY = rotation
                                                cameraDistance = 12f * density
                                            }
                                            .clickable { isFlipped = !isFlipped },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (rotation > 90f) Obsidian else ShimmeringGold
                                        ),
                                        border = BorderStroke(1.dp, if (rotation > 90f) SoftViolet else Obsidian)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (rotation > 90f) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier
                                                        .graphicsLayer { rotationY = 180f }
                                                        .padding(4.dp)
                                                ) {
                                                    Icon(Icons.Filled.AutoAwesome, null, tint = DustyRose, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = cardName,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Center,
                                                        fontSize = 11.sp,
                                                        lineHeight = 14.sp
                                                    )
                                                }
                                            } else {
                                                Icon(Icons.Filled.FilterNone, null, tint = Obsidian, modifier = Modifier.size(24.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (isTarotLoading) {
                                ShimmerLoading(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(12.dp)))
                            } else if (tarotReading != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Obsidian)
                                        .padding(14.dp)
                                ) {
                                    Text("Your Sacred Guidance", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = tarotReading!!,
                                        color = LightText,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.drawTarotCards() },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmObsidian, contentColor = ShimmeringGold),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, ShimmeringGold),
                                    modifier = Modifier.fillMaxWidth().height(42.dp)
                                ) {
                                    Text("Redraw Cards", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else { // MBTI Tab
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CosmicViolet.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Psychology, "MBTI", tint = ShimmeringGold, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("GL Fanatic MBTI Personality", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!profile?.mbtiResult.isNullOrEmpty()) {
                            val code = profile!!.mbtiResult!!
                            val tropeInfo = mbtiTropes[code] ?: Pair("The Celestial Observer", "You have a unique outlook on Girls' Love, enjoying complex bonds, deep subtext, and stellar chemistry.")

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Obsidian)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = code,
                                    color = ShimmeringGold,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tropeInfo.first,
                                    color = DustyRose,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = tropeInfo.second,
                                    color = LightText,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.updateProfile(mbti = "") }, // Reset
                                colors = ButtonDefaults.buttonColors(containerColor = Obsidian, contentColor = ShimmeringGold),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ShimmeringGold),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("Retake Questionnaire", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            val questions = listOf(
                                "Q1: When watching a series, you prefer..." to ("Action & Complex Plots" to "Intense Romance & Character Chemistry"),
                                "Q2: In community discussions, you usually..." to ("Read silently and analyze" to "Post your theories and hype"),
                                "Q3: You tend to like characters who are..." to ("Logical, Reserved & Cold" to "Warm, Devoted & Expressive"),
                                "Q4: Your release preference for new episodes is..." to ("A structured weekly release" to "A complete surprise drop")
                            )

                            Text(
                                "Discover your specialized Girls' Love viewer archetype by answering 4 simple questions below!",
                                color = LightText,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            questions.forEachIndexed { index, (q, options) ->
                                Text(q, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val isA = mbtiAnswers[index] == "A"
                                    val isB = mbtiAnswers[index] == "B"
                                    
                                    Button(
                                        onClick = { viewModel.answerMbtiQuestion(index, "A") },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isA) DustyRose else Obsidian,
                                            contentColor = if (isA) Color.White else LightText
                                        ),
                                        border = BorderStroke(1.dp, if (isA) DustyRose else Color.Transparent)
                                    ) {
                                        Text(options.first, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.answerMbtiQuestion(index, "B") },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isB) DustyRose else Obsidian,
                                            contentColor = if (isB) Color.White else LightText
                                        ),
                                        border = BorderStroke(1.dp, if (isB) DustyRose else Color.Transparent)
                                    ) {
                                        Text(options.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Button(
                                onClick = { viewModel.calculateMbti() },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ShimmeringGold,
                                    contentColor = Obsidian,
                                    disabledContainerColor = Obsidian.copy(alpha = 0.5f),
                                    disabledContentColor = MutedText
                                ),
                                enabled = mbtiAnswers.size == questions.size
                            ) {
                                Text("Calculate Viewer Badge", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(120.dp)) }
    }

    // Sign Picker Dialog
    if (showSignPicker) {
        AlertDialog(
            onDismissRequest = { showSignPicker = false },
            title = {
                Text(
                    text = "Pick Your $activeSignType Sign",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            containerColor = WarmObsidian,
            textContentColor = LightText,
            titleContentColor = Color.White,
            text = {
                Column {
                    Text(
                        text = "Align your celestial chart with your true cosmic resonance. ✨",
                        fontSize = 12.sp,
                        color = LightText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        zodiacSigns.chunked(3).forEach { rowSigns ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowSigns.forEach { sign ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Obsidian)
                                            .border(
                                                width = 1.dp,
                                                color = ShimmeringGold.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                if (activeSignType == "Sun") {
                                                    viewModel.updateProfile(sunSign = sign)
                                                    viewModel.fetchHoroscope(sign)
                                                } else if (activeSignType == "Moon") {
                                                    viewModel.updateProfile(moonSign = sign)
                                                } else {
                                                    viewModel.updateProfile(risingSign = sign)
                                                }
                                                showSignPicker = false
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = sign,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSignPicker = false }) {
                    Text("Close", color = ShimmeringGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showEditFavorites) {
        var editSeries by remember { mutableStateOf(profile?.favoriteSeries ?: "Blank: The Series") }
        var editCouple by remember { mutableStateOf(profile?.favoriteCouple ?: "FayeYoko") }
        var editActress by remember { mutableStateOf(profile?.favoriteActress ?: "Faye Peraya") }
        var editBias by remember { mutableStateOf(profile?.bias ?: "Yoko Apasra") }

        AlertDialog(
            onDismissRequest = { showEditFavorites = false },
            containerColor = Obsidian,
            title = { Text("Edit Favorites", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editSeries,
                        onValueChange = { editSeries = it },
                        label = { Text("Favorite Series", color = MutedText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = ShimmeringGold
                        )
                    )
                    OutlinedTextField(
                        value = editCouple,
                        onValueChange = { editCouple = it },
                        label = { Text("Favorite Couple", color = MutedText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = DustyRose
                        )
                    )
                    OutlinedTextField(
                        value = editActress,
                        onValueChange = { editActress = it },
                        label = { Text("Favorite Actress", color = MutedText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Lavender
                        )
                    )
                    OutlinedTextField(
                        value = editBias,
                        onValueChange = { editBias = it },
                        label = { Text("My Bias", color = MutedText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = WarmObsidian,
                            unfocusedContainerColor = WarmObsidian,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = SoftViolet
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateProfile(
                        favoriteSeries = editSeries,
                        favoriteCouple = editCouple,
                        favoriteActress = editActress,
                        bias = editBias
                    )
                    showEditFavorites = false
                }) {
                    Text("Save", color = ShimmeringGold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditFavorites = false }) {
                    Text("Cancel", color = MutedText)
                }
            }
        )
    }
}

@Composable
fun PostDetailScreen(navController: NavHostController, postId: String, viewModel: ProfileViewModel = viewModel()) {
    LaunchedEffect(postId) {
        viewModel.fetchPostDetails(postId)
    }
    
    val post by viewModel.selectedPost.collectAsStateWithLifecycle()
    var newComment by remember { mutableStateOf("") }
    
    // We will use local state for comments array just to demonstrate adding in the UI easily,
    // though realistically this should be a POST to the backend and a re-fetch.
    var localComments by remember { mutableStateOf<List<String>>(emptyList()) }
    
    LaunchedEffect(post) {
        if (post != null) {
            localComments = post?.comments?.map { it.content } ?: emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Obsidian)) {
        // Top Bar (Back button)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
            }
            Text("Thread", color = LightText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        if (post == null) {
            Box(modifier = Modifier.fillMaxSize().imePadding(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ShimmeringGold)
            }
            return@Column
        }
        
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            item {
                // Original Post
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(model = getStableAvatarUrl(post?.author?.name ?: "Unknown"), contentDescription = "Avatar", modifier = Modifier.size(48.dp).clip(CircleShape).background(Lavender), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(post?.author?.name ?: "Unknown", color = LightText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("2 hours ago", color = MutedText, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        val fullContent = post?.content ?: ""
                        val videoParts = fullContent.split("\n\n📹 [Attached Video](")
                        val hasVideo = videoParts.size > 1
                        val contentWithoutVideo = videoParts[0]
                        val videoUrl = if (hasVideo) videoParts[1].replace(")", "") else null

                        val imageParts = contentWithoutVideo.split("\n\n🖼️ [Attached Image](")
                        val hasImage = imageParts.size > 1
                        val displayText = imageParts[0]
                        val imageUrl = if (hasImage) imageParts[1].replace(")", "") else null

                        Text(displayText, color = LightText, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)

                        imageUrl?.let { url ->
                            Spacer(modifier = Modifier.height(12.dp))
                            AsyncImage(
                                model = url,
                                contentDescription = "Attached Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                             )
                        }

                        videoUrl?.let { url ->
                            Spacer(modifier = Modifier.height(12.dp))
                            VideoPlayerPlaceholder(url = url)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                                Icon(Icons.Filled.FavoriteBorder, "Like", tint = MutedText)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post?.upvotes ?: 0}", color = MutedText)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.ChatBubbleOutline, "Comment", tint = MutedText)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${localComments.size}", color = MutedText)
                            }
                        }
                    }
                }
                
                HorizontalDivider(color = WarmObsidian)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            items(localComments) { comment ->
                Row(modifier = Modifier.padding(bottom = 16.dp)) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(SoftViolet), contentAlignment = Alignment.Center) {
                        Text("C", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Commenter", color = LightText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Just now", color = MutedText, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(comment, color = LightText, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.KeyboardArrowUp, "Upvote", tint = MutedText, modifier = Modifier.size(16.dp).clickable { })
                            Text("1", color = MutedText, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
                            Icon(Icons.Filled.KeyboardArrowDown, "Downvote", tint = MutedText, modifier = Modifier.size(16.dp).clickable { })
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Reply", color = MutedText, fontSize = 12.sp, modifier = Modifier.clickable { })
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
        
        // Comment Input
        Box(modifier = Modifier.fillMaxWidth().background(WarmObsidian).padding(16.dp)) {
            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Add a comment...", color = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShimmeringGold,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = LightText,
                    unfocusedTextColor = LightText,
                    focusedContainerColor = Obsidian,
                    unfocusedContainerColor = Obsidian
                ),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    IconButton(onClick = { 
                        if (newComment.isNotBlank()) {
                            localComments = localComments + newComment
                            newComment = ""
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = ShimmeringGold)
                    }
                }
            )
        }
    }
}

@Composable
fun ShimmerLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(com.example.ui.romanticShimmerBrush()))
}
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    var dob by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf(false) }
    var showVerification by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showVerification) {
            Icon(Icons.Filled.MarkEmailRead, contentDescription = null, tint = ShimmeringGold, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Verification Sent", color = LightText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("We've sent a transactional validation link to your email. Please verify to continue.", color = MutedText, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("I have verified my email", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        } else if (step == 0) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = ShimmeringGold, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Age Verification", color = LightText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("You must be 13 or older to access SameSky.", color = MutedText)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it; ageError = false },
                label = { Text("Date of Birth (YYYY-MM-DD)", color = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShimmeringGold,
                    unfocusedBorderColor = WarmObsidian,
                    focusedTextColor = LightText,
                    unfocusedTextColor = LightText,
                    focusedContainerColor = Obsidian,
                    unfocusedContainerColor = Obsidian
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (ageError) {
                Text("Access denied. You must be at least 13 years old.", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    try {
                        val year = dob.take(4).toInt()
                        if (2026 - year >= 13) {
                            step = 1
                        } else {
                            ageError = true
                        }
                    } catch (e: Exception) {
                        ageError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Verify Age", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        } else if (step == 1) {
            Text("Create Account", color = LightText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Full Name", color = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = LightText, unfocusedTextColor = LightText, focusedBorderColor = ShimmeringGold, focusedContainerColor = Obsidian, unfocusedContainerColor = Obsidian),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email Address", color = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = LightText, unfocusedTextColor = LightText, focusedBorderColor = ShimmeringGold, focusedContainerColor = Obsidian, unfocusedContainerColor = Obsidian),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Secure Password", color = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = LightText, unfocusedTextColor = LightText, focusedBorderColor = ShimmeringGold, focusedContainerColor = Obsidian, unfocusedContainerColor = Obsidian),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = country, onValueChange = { country = it },
                label = { Text("Country of Residence", color = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = LightText, unfocusedTextColor = LightText, focusedBorderColor = ShimmeringGold, focusedContainerColor = Obsidian, unfocusedContainerColor = Obsidian),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = language, onValueChange = { language = it },
                label = { Text("Preferred Language", color = MutedText) },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = LightText, unfocusedTextColor = LightText, focusedBorderColor = ShimmeringGold, focusedContainerColor = Obsidian, unfocusedContainerColor = Obsidian),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { showVerification = true },
                colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Register", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SubscriptionBuilderScreen(onBack: () -> Unit) {
    var months by remember { mutableStateOf(1f) }
    var selectedTier by remember { mutableStateOf(1) }
    
    val tiers = listOf(
        Triple("Stardust", 4.99, "Ad-free browsing & basic badge"),
        Triple("Moonbeam", 9.99, "Exclusive GL content & 100 bonus coins/mo"),
        Triple("Supernova", 19.99, "Offline viewing & 500 bonus coins/mo")
    )
    
    val currentTier = tiers[selectedTier]
    val pricePerMonth = currentTier.second
    val totalPrice = (months.toInt() * pricePerMonth).let { String.format("%.2f", it) }
    
    val badge = when (selectedTier) {
        0 -> "Stardust Member"
        1 -> "Moonbeam Supporter"
        else -> "Supernova VIP"
    }
    
    val bonusCoins = months.toInt() * when (selectedTier) {
        0 -> 0
        1 -> 100
        else -> 500
    }

    Column(modifier = Modifier.fillMaxSize().background(Obsidian).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
            }
            Text("Subscription Builder", color = ShimmeringGold, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Select Cosmic Tier", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            itemsIndexed(tiers) { index, tier ->
                val isSelected = selectedTier == index
                Card(
                    modifier = Modifier.width(220.dp).clickable { selectedTier = index },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) DustyRose else WarmObsidian),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, if (isSelected) ShimmeringGold else Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    ) {
                        Text(tier.first, color = if (isSelected) Obsidian else LightText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${tier.second}/mo", color = if (isSelected) Obsidian else ShimmeringGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Plan features
                        val features = tier.third.split(" & ")
                        features.forEach { feature ->
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 4.dp)) {
                                Icon(Icons.Filled.Check, contentDescription = "Included", tint = if (isSelected) Obsidian else ShimmeringGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(feature, color = if (isSelected) Obsidian else MutedText, fontSize = 12.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { selectedTier = index },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Obsidian else CosmicViolet)
                        ) {
                            Text(if (isSelected) "Current Choice" else "Select Tier", color = if (isSelected) ShimmeringGold else Color.White)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Select Duration", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = months,
            onValueChange = { months = it },
            valueRange = 1f..12f,
            steps = 10,
            colors = SliderDefaults.colors(
                thumbColor = ShimmeringGold,
                activeTrackColor = ShimmeringGold,
                inactiveTrackColor = WarmObsidian
            )
        )
        Text("${months.toInt()} Month(s)", color = ShimmeringGold, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = WarmObsidian),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Checkout Summary", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Selected Tier:", color = MutedText)
                    Text(currentTier.first, color = LightText, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Total Price:", color = MutedText)
                    Text("$$totalPrice", color = LightText, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Target Loyalty Badge:", color = MutedText)
                    Text(badge, color = ShimmeringGold, fontWeight = FontWeight.Bold)
                }
                if (bonusCoins > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Bonus Coins:", color = MutedText)
                        Text("+$bonusCoins", color = ShimmeringGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Checkout securely via Stripe", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun VirtualCoinShopScreen(onBack: () -> Unit) {
    val coinPacks = listOf(
        Pair(100, "$0.99"),
        Pair(500, "$4.99"),
        Pair(1000, "$8.99"),
        Pair(5000, "$39.99")
    )
    Column(modifier = Modifier.fillMaxSize().background(Obsidian).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
            }
            Text("Virtual Coin Shop", color = ShimmeringGold, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your Balance: 1,250 Coins", color = LightText, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(coinPacks) { pack ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { },
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, "Coins", tint = ShimmeringGold, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("${pack.first} Coins", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(pack.second, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun LeaderboardScreen() {
    Column(modifier = Modifier.fillMaxSize().background(Obsidian).padding(16.dp)) {
        Text("Global Leaderboard", color = ShimmeringGold, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Top Gifters (All-Time)", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        PodiumDisplay(first = "LinglingFan99", second = "OrmLover", third = "FreenBecky4Ever")
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("Top Reviewers", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        PodiumDisplay(first = "GL_Critique", second = "DramaWatcher", third = "SapphicStan")
    }
}

@Composable
fun PodiumDisplay(first: String, second: String, third: String) {
    Row(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // Silver (Second Place)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFC0C0C0)), contentAlignment = Alignment.Center) {
                Text("2", color = Obsidian, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(second, color = LightText, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(80.dp).height(80.dp).background(Color(0xFFC0C0C0), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)))
        }
        
        // Gold (First Place)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(ShimmeringGold), contentAlignment = Alignment.Center) {
                Text("1", color = Obsidian, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(first, color = ShimmeringGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(80.dp).height(110.dp).background(ShimmeringGold, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)))
        }
        
        // Bronze/Rose Gold (Third Place)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(DustyRose), contentAlignment = Alignment.Center) {
                Text("3", color = Obsidian, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(third, color = LightText, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(80.dp).height(60.dp).background(DustyRose, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)))
        }
    }
}
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MediaProfileScreen(
    navController: NavHostController, 
    mediaId: String,
    dashboardViewModel: DashboardViewModel = viewModel(viewModelStoreOwner = androidx.activity.compose.LocalActivity.current as androidx.activity.ComponentActivity)
) {
    var userRating by remember { mutableStateOf(0) }
    var userReview by remember { mutableStateOf("") }
    var isWatchlist by remember { mutableStateOf(false) }

    val contentEntries by dashboardViewModel.contentEntries.collectAsStateWithLifecycle()
    val supabaseSeriesList by dashboardViewModel.supabaseSeries.collectAsStateWithLifecycle()

    val allSeries = remember(contentEntries, supabaseSeriesList) {
        val localGL = parseMediaItems(GL_SERIES_JSON)
        val localLesbian = parseMediaItems(LESBIAN_SERIES_JSON)
        val localMovies = parseMediaItems(GLOBAL_MOVIES_JSON)
        
        val fetchedMapped = contentEntries.map {
            MediaItem(
                title = it.title,
                thumbnail = getStableImageUrl(it.title),
                region = it.sunSign ?: "Global",
                releaseYear = it.mbti ?: "2024",
                description = it.content,
                priority = "Medium"
            )
        }
        
        val supabaseMapped = supabaseSeriesList.map {
            MediaItem(
                title = it.title,
                thumbnail = it.image_url,
                region = it.region ?: "International",
                releaseYear = it.release_year ?: "2024",
                description = it.description,
                priority = it.priority ?: "Low"
            )
        }
        
        (localGL + localLesbian + localMovies + fetchedMapped + supabaseMapped).distinctBy { it.title }
    }

    val selectedMedia = remember(allSeries, mediaId) {
        allSeries.find { it.title.equals(mediaId, ignoreCase = true) }
    }

    val title = selectedMedia?.title ?: mediaId.replace("_", " ")
    val subtitle = if (selectedMedia != null) "Region: ${selectedMedia.region} • Priority: ${selectedMedia.priority}" else "GL Drama Recommendation"
    val yearInfo = if (selectedMedia != null) "${selectedMedia.releaseYear} • Celestial Choice" else "2024 • Community Choice"
    val description = selectedMedia?.description ?: "A popular GL drama series highly recommended by the SameSky celestial community. Explore the beautiful chemistry, narrative depth, and incredible community reactions."
    val imageUrl = selectedMedia?.thumbnail ?: "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=800&auto=format&fit=crop"

    Column(modifier = Modifier.fillMaxSize().background(Obsidian).verticalScroll(rememberScrollState())) {
        // Hero Image with floating back button and gradient overlay
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Obsidian
                            )
                        )
                    )
            )
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Obsidian.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
            }
        }
        
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = ShimmeringGold, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, color = SoftViolet, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 4.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = "Rating", tint = ShimmeringGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("9.5 Fan Rating", color = LightText, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(16.dp))
                Text(yearInfo, color = MutedText, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(description, color = LightText, style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { isWatchlist = !isWatchlist },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isWatchlist) SoftViolet else WarmObsidian, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(if (isWatchlist) Icons.Filled.Check else Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isWatchlist) "In Watchlist" else "Add to Watchlist")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Leave a Review", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // 1-10 Rating
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                for (i in 1..10) {
                    Text(
                        text = i.toString(),
                        color = if (i <= userRating) ShimmeringGold else MutedText,
                        modifier = Modifier.clickable { userRating = i }.padding(4.dp),
                        fontWeight = if (i <= userRating) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = userReview,
                onValueChange = { userReview = it },
                placeholder = { Text("Write your review (Optional for Premium)", color = MutedText) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShimmeringGold,
                    unfocusedBorderColor = WarmObsidian,
                    focusedTextColor = LightText,
                    unfocusedTextColor = LightText,
                    focusedContainerColor = WarmObsidian,
                    unfocusedContainerColor = WarmObsidian
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Submit Review */ },
                colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit Review", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("More Like This", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(listOf("Blank The Series", "23.5", "Gap The Series")) { similar ->
                    Card(modifier = Modifier.width(140.dp).height(200.dp), colors = CardDefaults.cardColors(containerColor = WarmObsidian)) {
                        Box(modifier = Modifier.fillMaxSize().imePadding(), contentAlignment = Alignment.Center) {
                            Text(similar, color = LightText, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GLDirectoryScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("Popular Thai GL ships from recent 2025 and 2026 series") }
    var isLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<GLShipInfo>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(Obsidian).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("GL Directory", color = ShimmeringGold, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Real-time AI Verified Ship Database", color = MutedText, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = WarmObsidian),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AI Grounded Search",
                    color = LightText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Search the web with Gemini to pull the absolute latest factual relationships, series, and status.",
                    color = MutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightText,
                        unfocusedTextColor = LightText,
                        focusedBorderColor = ShimmeringGold,
                        unfocusedBorderColor = MutedText.copy(alpha = 0.5f),
                        focusedContainerColor = Obsidian,
                        unfocusedContainerColor = Obsidian
                    ),
                    placeholder = { Text("Search for GL couples/series...", color = MutedText) },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                try {
                                    val results = getGLShipsData(searchQuery)
                                    if (results != null) {
                                        searchResults = results
                                    } else {
                                        errorMessage = "No verified data returned. Please try a different query."
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Obsidian, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Grounding & Verifying...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search & Ground with AI", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33FF0000)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Error, contentDescription = "Error", tint = Color.Red)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(errorMessage!!, color = Color.Red, fontSize = 14.sp)
                        }
                    }
                }
            }
            
            val results = searchResults
            if (results != null && results.isNotEmpty()) {
                item {
                    Text(
                        text = "AI Verified Results",
                        color = ShimmeringGold,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(results) { ship ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ship.shipName,
                                    color = ShimmeringGold,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .background(CosmicViolet.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .border(1.dp, CosmicViolet, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = ship.status.uppercase(),
                                        color = LightText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.People, contentDescription = null, tint = BlushPink, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ship.characters.joinToString(" & "),
                                    color = LightText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Movie, contentDescription = null, tint = SoftViolet, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ship.seriesTitle,
                                    color = MutedText,
                                    fontSize = 14.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = ship.summary,
                                color = LightText,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Archived Community Ships",
                    color = LightText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            val staticShips = listOf(
                Triple("LingOrm", "Lingling & Orm", listOf("official canon" to Color.Red, "floating" to ShimmeringGold)),
                Triple("FreenBecky", "Freen & Becky", listOf("official ship" to Color.Green, "submarine" to Color(0xFFC0C0C0))),
                Triple("Englot", "Engfa & Charlotte", listOf("rumored ship" to Color(0xFFFFC0CB), "colloquial" to Color(0xFF000080)))
            )
            
            items(staticShips) { ship ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.MediaProfile.createRoute(ship.first)) },
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(ship.first, color = LightText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(ship.second, color = MutedText, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ship.third.forEach { tag ->
                                Box(modifier = Modifier.background(tag.second.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).border(1.dp, tag.second, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(tag.first.uppercase(), color = tag.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RaffleCard(title: String, type: String, tint: Color) {
    Card(
        modifier = Modifier.width(240.dp).height(120.dp),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocalActivity, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(type, color = tint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = LightText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = tint, contentColor = Obsidian),
                modifier = Modifier.fillMaxWidth().height(32.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Enter Raffle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CareerHubScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().background(Obsidian).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
            }
            Text("Career Hub", color = ShimmeringGold, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WarmObsidian),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Verified Creator Status", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Independent community creators can apply for Verified Creator Status to unlock native live video streaming, virtual gifting monetization features, and fan letter pools.", color = MutedText, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Apply for Verified Status", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Centralized Job Board", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        val jobs = listOf("Community Manager - TH", "Content Moderator (Part-time)", "Graphic Designer (Freelance)")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(jobs) { job ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(job, color = LightText, fontWeight = FontWeight.Bold)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ShimmeringGold)
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ActressCard(name: String) {
    var showConfig by remember { mutableStateOf(false) }
    var newPost by remember { mutableStateOf(true) }
    var liveStart by remember { mutableStateOf(true) }
    var newRaffle by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.Gray))
            Spacer(modifier = Modifier.height(12.dp))
            Text(name, color = LightText, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { }, modifier = Modifier.weight(1f).height(32.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian)) {
                    Text("Follow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showConfig = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Settings, "Config", tint = MutedText)
                }
            }
        }
    }

    if (showConfig) {
        ModalBottomSheet(onDismissRequest = { showConfig = false }, containerColor = WarmObsidian) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text("Notification Settings", color = ShimmeringGold, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("New Social Posts", color = LightText)
                    Switch(checked = newPost, onCheckedChange = { newPost = it }, colors = SwitchDefaults.colors(checkedThumbColor = ShimmeringGold))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("Live Stream Starts", color = LightText)
                    Switch(checked = liveStart, onCheckedChange = { liveStart = it }, colors = SwitchDefaults.colors(checkedThumbColor = ShimmeringGold))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("New Raffles Launched", color = LightText)
                    Switch(checked = newRaffle, onCheckedChange = { newRaffle = it }, colors = SwitchDefaults.colors(checkedThumbColor = ShimmeringGold))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun VideoPlayerPlaceholder(url: String) {
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                delay(100)
                playbackProgress = (playbackProgress + 0.015f) % 1.0f
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { isPlaying = !isPlaying },
        colors = CardDefaults.cardColors(containerColor = WarmObsidian)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = url,
                contentDescription = "Video Thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (isPlaying) 0.3f else 0.5f))
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = ShimmeringGold,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isPlaying) "Playing Celestial Clip..." else "Tap to Play Video",
                    color = ShimmeringGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                LinearProgressIndicator(
                    progress = { playbackProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = DustyRose,
                    trackColor = Color.Gray.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("0:${String.format("%02d", (playbackProgress * 30).toInt())} / 0:30", color = LightText, fontSize = 11.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Audio", tint = LightText, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Filled.Fullscreen, contentDescription = "Fullscreen", tint = LightText, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavHostController, viewModel: ProfileViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var postContent by remember { mutableStateOf("") }
    
    val imagePresets = listOf(
        "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1464802686167-b939a6910659?q=80&w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?q=80&w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1538370965046-79c0d6907d47?q=80&w=600&auto=format&fit=crop"
    )
    var selectedImageUrl by remember { mutableStateOf(imagePresets[0]) }

    val videoPresets = listOf(
        "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?q=80&w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?q=80&w=600&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1516339901601-2e1b62dc0c45?q=80&w=600&auto=format&fit=crop"
    )
    var selectedVideoUrl by remember { mutableStateOf(videoPresets[0]) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Obsidian
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Post",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val tabs = listOf("Text", "Image", "Video")
                    val tabIcons = listOf(Icons.AutoMirrored.Filled.TextSnippet, Icons.Filled.Image, Icons.Filled.Videocam)
                    
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DustyRose else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tabIcons[index],
                                    contentDescription = title,
                                    tint = if (isSelected) Color.White else MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    color = if (isSelected) Color.White else MutedText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("POST CONTENT", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = postContent,
                onValueChange = { postContent = it },
                placeholder = { Text("What is on your celestial mind? Share with the GL universe... 🌌", color = MutedText, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = LightText,
                    focusedContainerColor = WarmObsidian,
                    unfocusedContainerColor = WarmObsidian,
                    focusedBorderColor = ShimmeringGold,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 1) {
                Text("SELECT BACKGROUND IMAGE", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(imagePresets) { img ->
                        val isChosen = selectedImageUrl == img
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isChosen) 3.dp else 1.5.dp,
                                    color = if (isChosen) ShimmeringGold else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedImageUrl = img }
                        ) {
                            AsyncImage(
                                model = img,
                                contentDescription = "Preset image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else if (selectedTab == 2) {
                Text("SELECT VIDEO THEME", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(videoPresets) { vid ->
                        val isChosen = selectedVideoUrl == vid
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isChosen) 3.dp else 1.5.dp,
                                    color = if (isChosen) ShimmeringGold else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedVideoUrl = vid }
                        ) {
                            AsyncImage(
                                model = vid,
                                contentDescription = "Preset video",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("LIVE PREVIEW", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = getStableAvatarUrl("You"),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("You", color = LightText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Just Now • Preview", color = MutedText, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (postContent.isBlank()) "Write something above to see your post come alive..." else postContent,
                        color = if (postContent.isBlank()) MutedText else LightText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    
                    if (selectedTab == 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = selectedImageUrl,
                            contentDescription = "Preview Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else if (selectedTab == 2) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = selectedVideoUrl,
                                contentDescription = "Preview Video",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = ShimmeringGold, modifier = Modifier.size(40.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (postContent.isNotBlank()) {
                        val formattedContent = when (selectedTab) {
                            1 -> "${postContent}\n\n🖼️ [Attached Image](${selectedImageUrl})"
                            2 -> "${postContent}\n\n📹 [Attached Video](${selectedVideoUrl})"
                            else -> postContent
                        }

                        viewModel.addPost(formattedContent, authorName = "You")
                        navController.navigate(Screen.Community.route) {
                            popUpTo(Screen.Marquee.route)
                        }
                    }
                },
                enabled = postContent.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DustyRose,
                    contentColor = Color.White,
                    disabledContainerColor = WarmObsidian.copy(alpha = 0.5f),
                    disabledContentColor = MutedText
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Publish Post", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun MoreHubScreen(navController: NavHostController) {
    val hubItems = listOf(
        HubItem("GL Directory", "Explore series and favorite pairings.", Icons.Filled.Collections, CosmicViolet, Screen.GLDirectory.route),
        HubItem("Coin Shop", "Purchase stars and unlock badges.", Icons.Filled.MonetizationOn, ShimmeringGold, Screen.CoinShop.route),
        HubItem("TMDb Movie Watchlist", "Search TMDb & curate your movie watchlist.", Icons.Filled.Movie, ShimmeringGold, Screen.Watchlist.route),
        HubItem("Career Hub", "Find GL roles, writers, and crews.", Icons.Filled.Work, SoftViolet, Screen.CareerHub.route),
        HubItem("AI Lab (Gemini)", "Chatbots, Image Gen, Search Grounding", Icons.Filled.Science, ShimmeringGold, Screen.AILab.route),
        HubItem("Stars Premium", "Unlock cosmic access to premium features.", Icons.Filled.Star, DustyRose, Screen.Subscription.route),
        HubItem("Leaderboards", "View top authors and lists.", Icons.Filled.EmojiEvents, ShimmeringGold, Screen.Leaderboard.route),
        HubItem("Match Astro", "Astrology pairings and compatibility.", Icons.Filled.Dashboard, CosmicViolet, Screen.UserDashboard.route),
        HubItem("Admin Control", "Moderator settings and configs.", Icons.Filled.AdminPanelSettings, SoftViolet, Screen.AdminDashboard.route)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Celestial Hub",
                color = ShimmeringGold,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Discover features beyond the primary screens of SameSky. Expand your girls' love journey! ✨",
                color = LightText,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        items(hubItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { navController.navigate(item.route) },
                colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(item.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            color = LightText,
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Navigate",
                        tint = MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

data class HubItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val route: String
)

@Composable
fun PollArchiveScreen(navController: NavHostController, viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    
    var selectedFilter by remember { mutableStateOf("Newest") }
    val filters = listOf("Most Voted", "Newest", "Oldest")
    
    val pollList = remember { listOf("archive_1", "archive_2", "archive_3") }
    
    val sortedPolls = remember(selectedFilter) {
        when(selectedFilter) {
            "Oldest" -> pollList.reversed()
            "Most Voted" -> pollList.sortedByDescending { it.hashCode() }
            else -> pollList
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
            }
            Text(
                text = "Poll Archive",
                color = ShimmeringGold,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ShimmeringGold,
                        selectedLabelColor = WarmObsidian,
                        labelColor = LightText
                    )
                )
            }
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sortedPolls) { pollId ->
                com.example.ui.PollCard(
                    pollId = pollId,
                    currentUserId = profile?.id?.toString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
