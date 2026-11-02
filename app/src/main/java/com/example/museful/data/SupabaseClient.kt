package com.example.museful.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.gotrue.Auth

val supabase = createSupabaseClient(
    supabaseUrl = "https://onxsmzpjknsyixwxoqqk.supabase.co",
    supabaseKey = "sb_publishable_BGqw5xvcgQ4n86n-0VXWIw_BuOeYQyX"
) {
    install(Auth)
    install(Postgrest)
}