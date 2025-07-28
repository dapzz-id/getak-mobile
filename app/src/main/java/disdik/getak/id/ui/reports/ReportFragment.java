    package disdik.getak.id.ui.reports;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;

    import disdik.getak.id.AddReportsActivity;
    import disdik.getak.id.DashboardActivity;
    import disdik.getak.id.databinding.FragmentReportsBinding;

    public class ReportFragment extends Fragment {

        private FragmentReportsBinding binding;
        private Button reportButton;

        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            ReportViewModel reportViewModel =
                    new ViewModelProvider(this).get(ReportViewModel.class);

            binding = FragmentReportsBinding.inflate(inflater, container, false);
            View root = binding.getRoot();
            binding.btnTambah.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), AddReportsActivity.class);
                startActivity(intent);
            });
            return root;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }